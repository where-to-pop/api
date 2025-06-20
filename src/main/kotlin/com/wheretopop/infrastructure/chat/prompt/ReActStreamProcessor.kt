package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatMessageId
import com.wheretopop.infrastructure.chat.ChatAssistant
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyExecutionType
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * ReAct 스트림 처리를 담당하는 클래스
 */
@Component
class ReActStreamProcessor(
    private val contextOptimizer: ContextOptimizer,
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>,
    private val tokenUsageTracker: TokenUsageTracker
) {
    private val logger = KotlinLogging.logger {}
    
    fun executeMultiStepPlanStream(
        chat: Chat,
        chatMessageId: ChatMessageId,
        plan: ReActExecutionInput,
        originalUserMessage: String,
    ): Flow<ReActStreamResponse> = flow {
        val stepResults = ConcurrentHashMap<Int, String>()
        val totalSteps = plan.reActResponse.actions.size
        
        logger.info("스트림 실행 시작: 총 ${totalSteps}단계")
        
        // RAG 패턴: R+A (배치 처리) → G (스트리밍)
        val ragSteps = separateRAGSteps(plan.reActResponse.actions)
        
        var retrievalResult: Map<Int, ChatAssistant.ResponseWithToolExecutionResult> = emptyMap()
        var augmentationResult: Map<Int, ChatAssistant.ResponseWithToolExecutionResult> = emptyMap()


        if (ragSteps.retrievalSteps.isNotEmpty()) {
            retrievalResult = executeBatchSteps(
                chat, chatMessageId, plan.requirementAnalysis, ragSteps.retrievalSteps,
                originalUserMessage, stepResults, totalSteps
            ) { response -> emit(response) }
            
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chat.id.toString(),
                    executionId = chatMessageId.toString(),
                    phase = ExecutionPhase.AGGREGATING,
                    currentStep = null,
                    totalSteps = totalSteps,
                    progress = 0.75,
                    message = "정보 수집을 완료하였어요!"
                )
            ))
        }
        if (ragSteps.augmentationSteps.isNotEmpty()) {
            augmentationResult = executeBatchSteps(
                chat, chatMessageId, plan.requirementAnalysis, ragSteps.augmentationSteps,
                originalUserMessage, stepResults, totalSteps
            ) { response -> emit(response) }

            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chat.id.toString(),
                    executionId = chatMessageId.toString(),
                    phase = ExecutionPhase.AGGREGATING,
                    currentStep = null,
                    totalSteps = totalSteps,
                    progress = 0.75,
                    message = "답변을 작성할 준비가 되었어요!"
                )
            ))
        } else {
            augmentationResult = retrievalResult;
        }

        // G (Generation) 단계를 스트리밍으로 실행
        val generationStep = ragSteps.generationStep

        // 진행률 계산: R+A 단계가 있으면 0.78부터, 없으면 0.1부터 시작
        val startProgress = if (ragSteps.augmentationSteps.isNotEmpty()) 0.78 else 0.1
        
        // G 단계 시작 알림
        emit(ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chat.id.toString(),
                executionId = chatMessageId.toString(),
                phase = ExecutionPhase.STEP_EXECUTING,
                currentStep = generationStep.step,
                totalSteps = totalSteps,
                progress = startProgress,
                message = StrategyType.buildExecutingMessage(generationStep.strategy)
            )
        ))
        
        // 실시간 스트림으로 G 단계 실행
        val accumulatedResponse = StringBuilder()
        val optimizedContext = contextOptimizer.buildOptimizedContextWithChat(
            chat, plan.requirementAnalysis, currentStep = generationStep, stepResults
        );

        executeStepInternalStream(
            chatMessageId,
            step = generationStep,
            optimizedContext,
            originalUserMessage
        ).collect { chunk ->
            accumulatedResponse.append(chunk)
            
            // 진행률 계산: R+A가 있으면 78%~99%, 없으면 10%~99%
            val progressRange = if (ragSteps.augmentationSteps.isNotEmpty()) 0.21 else 0.89
            val currentProgress = startProgress + (accumulatedResponse.length.toDouble() / 1000) * progressRange
            
            // 실시간 AI 응답 청크를 그대로 전달
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chat.id.toString(),
                    executionId = chatMessageId.toString(),
                    phase = ExecutionPhase.AGGREGATING,
                    currentStep = generationStep.step,
                    totalSteps = totalSteps,
                    progress = currentProgress.coerceAtMost(0.99), // 최대 99%
                    message = StrategyType.buildExecutingMessage(generationStep.strategy)
                ),
                isComplete = false,
                finalResult = chunk
            ))
        }
        
        // 최종 완료 - 누적된 전체 응답을 함께 전송
        emit(ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chat.id.toString(),
                executionId = chatMessageId.toString(),
                phase = ExecutionPhase.COMPLETED,
                currentStep = null,
                totalSteps = totalSteps,
                progress = 1.0,
                message = StrategyType.buildPhaseMessage(ExecutionPhase.COMPLETED)
            ),
            isComplete = true,
            finalResult = accumulatedResponse.toString() // 누적된 전체 응답
        ))
    }
    
    /**
     * 개별 단계 실행 (스트림 버전) - AI 실시간 응답 스트림
     */
    private fun executeStepInternalStream(
        chatMessageId: ChatMessageId,
        step: ActionStep, 
        optimizedContext: String, 
        originalUserMessage: String,
    ): Flow<String> = flow {
        try {
            val strategyType = StrategyType.findById(step.strategy)
                ?: throw IllegalArgumentException("Unknown strategy: ${step.strategy}")
            
            val strategy = strategies.find { it.getType() == strategyType }
                ?: throw IllegalStateException("No strategy found for type: ${strategyType.id}")
            
            // 최적화된 프롬프트 생성 - Original User Message 포함
            val stepPrompt = """
                Original User Question: "$originalUserMessage"
                
                Execute: ${step.purpose}
                
                Context: $optimizedContext
                
                Provide a focused generation for: ${step.expected_output}
                
                Make sure to directly address the original user question.
            """.trimIndent()
            
            // 실제 AI 스트림 호출 - 실시간 청크를 그대로 전달
            chatAssistant.callStream(
                chatMessageId,
                strategy.createPrompt(stepPrompt), 
                strategy.getToolCallingChatOptions()
            ).collect { chatResponse ->
                val textChunk = chatResponse.chatResponse.result.output.text ?: ""
                if (textChunk.isNotEmpty()) {
                    logger.debug("Streaming chunk: '$textChunk'")
                    emit(textChunk) // 실시간 AI 응답 청크
                }
            }
                
        } catch (e: Exception) {
            logger.error("Step ${step.step} stream execution failed", e)
            throw e
        }
    }
    

    
    /**
     * 개별 단계 실행 (일반 버전) - 결과만 필요한 경우
     */
    private suspend fun executeStepInternal(
        chatMessageId: ChatMessageId,
        step: ActionStep,
        optimizedContext: String, 
        originalUserMessage: String
    ): ChatAssistant.ResponseWithToolExecutionResult {
        return withContext(Dispatchers.IO) {
            try {
                val strategyType = StrategyType.findById(step.strategy)
                    ?: throw IllegalArgumentException("Unknown strategy: ${step.strategy}")
                
                val strategy = strategies.find { it.getType() == strategyType }
                    ?: throw IllegalStateException("No strategy found for type: ${strategyType.id}")
                
                // 최적화된 프롬프트 생성 - Original User Message 포함
                val stepPrompt = """
                    Original User Question: "$originalUserMessage"
                    
                    Execute: ${step.purpose}
                    
                    Context: $optimizedContext
                    
                    Provide a focused generation for: ${step.expected_output}
                    
                    Make sure to directly address the original user question.
                """.trimIndent()
                
                // 일반 AI 호출 (결과만 필요)
                val response = chatAssistant.call(chatMessageId, strategy.createPrompt(stepPrompt), strategy.getToolCallingChatOptions())
                // 토큰 사용량 추적
                tokenUsageTracker.trackAndLogTokenUsage(response.chatResponse, "ReActStream 단계 ${step.step} - ${strategy.getType().id}")
                
                return@withContext response

            } catch (e: Exception) {
                logger.error("Step ${step.step} execution failed", e)
                throw e
            }
        }
    }
    
    /**
     * RAG 단계들을 분리합니다.
     */
    private fun separateRAGSteps(actions: List<ActionStep>): RAGSteps {
        val retrievalSteps = mutableListOf<ActionStep>()
        val augmentationSteps = mutableListOf<ActionStep>()

        var generationStep: ActionStep? = null
        
        actions.forEach { step ->
            val strategyType = StrategyType.findById(step.strategy)
            when (strategyType?.executionType) {
                StrategyExecutionType.RETRIEVAL,
                StrategyExecutionType.PRE_RETRIEVAL -> {
                    retrievalSteps.add(step)
                }
                StrategyExecutionType.AUGMENTATION -> {
                    augmentationSteps.add(step)
                }
                StrategyExecutionType.GENERATION -> {
                    generationStep = step
                }
                else -> {
                    logger.warn("Unknown strategy type for step ${step.step}: ${step.strategy}")
                    throw ErrorCode.COMMON_SYSTEM_ERROR.toException();
                }
            }
        }
        
        return RAGSteps(
            retrievalSteps,
            augmentationSteps,
            generationStep = generationStep ?: throw IllegalStateException("No generation step found!")
        )
    }
    
    /**
     * 단계들을 배치로 실행합니다.
     */
    private suspend fun executeBatchSteps(
        chat: Chat,
        chatMessageId: ChatMessageId,
        requirementAnalysis: RequirementAnalysis?,
        raSteps: List<ActionStep>,
        originalUserMessage: String,
        stepResults: ConcurrentHashMap<Int, String>,
        totalSteps: Int,
        emit: suspend (ReActStreamResponse) -> Unit
    ): Map<Int, ChatAssistant.ResponseWithToolExecutionResult> {
        val sortedSteps = raSteps.sortedBy { it.step }
        val results: MutableMap<Int, ChatAssistant.ResponseWithToolExecutionResult> = mutableMapOf()
        for (step in sortedSteps) {
            try {
                logger.info(" 실행: ${step.strategy}")

                // 🔄 단계 실행 시작 알림
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chat.id.toString(),
                        executionId = chatMessageId.toString(),
                        phase = ExecutionPhase.STEP_EXECUTING,
                        currentStep = step.step,
                        totalSteps = totalSteps,
                        progress = 0.6,
                        message = StrategyType.buildExecutingMessage(step.strategy, step.purpose)
                    )
                ))
                
                // 컨텍스트 최적화 (Chat 객체 활용)
                val optimizedContext = contextOptimizer.buildOptimizedContextWithChat(chat, requirementAnalysis, step , stepResults, )
                
                // 단계 실행
                val stepExecutionResult = executeStepInternal( chatMessageId, step, optimizedContext, originalUserMessage)
                results[step.step] = stepExecutionResult;
                val strategyType = StrategyType.findById(step.strategy)
                    ?: throw IllegalArgumentException("Unknown strategy: ${step.strategy}")
                if (strategyType.executionType === StrategyExecutionType.RETRIEVAL) {
                    stepResults[step.step] = stepExecutionResult.toolExecutionResult ?: "정보를 조회하지 못했습니다."
                } else {
                    stepResults[step.step] = stepExecutionResult.chatResponse.result.output.text ?: "응답을 받지 못했습니다."
                }

                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chat.id.toString(),
                        executionId = chatMessageId.toString(),
                        phase = ExecutionPhase.STEP_COMPLETED,
                        currentStep = step.step,
                        totalSteps = totalSteps,
                        progress = ((results.size.toDouble() + 1) / raSteps.size) * 0.7,
                        message = StrategyType.buildCompletedMessage(step.strategy, step.purpose),
                        stepResult = stepResults[step.step]?.take(100) // 미리보기용 100자
                    )
                ))
                
            } catch (e: Exception) {
                val errorResult = "단계 ${step.step} 실패: ${e.message}"
//                results[step.step] = errorResult
                stepResults[step.step] = errorResult
                
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chat.id.toString(),
                        executionId = chatMessageId.toString(),
                        phase = ExecutionPhase.STEP_FAILED,
                        currentStep = step.step,
                        totalSteps = totalSteps,
                        progress = ((results.size.toDouble() + 1) / raSteps.size) * 0.7,
                        message = StrategyType.buildErrorMessage(step.strategy, errorResult),
                        error = errorResult
                    )
                ))
            }
        }
        
        return results
    }
}