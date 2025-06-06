package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
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
        executionPlan: ReActResponse, 
        originalUserMessage: String,
        chatId: String,
        executionId: String
    ): Flow<ReActStreamResponse> = flow {
        val stepResults = ConcurrentHashMap<Int, String>()
        val totalSteps = executionPlan.actions.size
        
        logger.info("스트림 실행 시작: 총 ${totalSteps}단계")
        
        // RAG 패턴: R+A (배치 처리) → G (스트리밍)
        val ragSteps = separateRAGSteps(executionPlan.actions)
        
        val retrievalAugmentationResults: Map<Int, String>
        
        // R+A 단계가 있는 경우에만 실행
        if (ragSteps.retrievalAugmentationSteps.isNotEmpty()) {
            // R+A 단계들을 배치로 실행
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.STEP_EXECUTING,
                    currentStep = null,
                    totalSteps = totalSteps,
                    progress = 0.1,
                    message = "정보 수집 및 분석 단계 (R+A) 실행 중..."
                )
            ))
            
            retrievalAugmentationResults = executeRABatchSteps(
                chat, ragSteps.retrievalAugmentationSteps, originalUserMessage, stepResults,
                chatId, executionId, totalSteps
            ) { response -> emit(response) }
            
            // R+A 완료 알림
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.AGGREGATING,
                    currentStep = null,
                    totalSteps = totalSteps,
                    progress = 0.75,
                    message = "정보 수집 및 분석 완료. 답변을 작성할 준비가 되었어요!"
                )
            ))
        } else {
            // R+A 단계가 없는 경우 (SIMPLE 케이스)
            logger.info("R+A 단계 없음: 바로 응답 생성 단계로 진행")
            retrievalAugmentationResults = emptyMap()
        }
        
        // G (Generation) 단계를 스트리밍으로 실행
        val generationStep = ragSteps.generationStep
        val allRAResults = retrievalAugmentationResults.values.joinToString("\n\n") { result ->
            "Context: $result"
        }
        
        // 진행률 계산: R+A 단계가 있으면 0.78부터, 없으면 0.1부터 시작
        val startProgress = if (ragSteps.retrievalAugmentationSteps.isNotEmpty()) 0.78 else 0.1
        
        // G 단계 시작 알림
        emit(ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chatId,
                executionId = executionId,
                phase = ExecutionPhase.STEP_EXECUTING,
                currentStep = generationStep.step,
                totalSteps = totalSteps,
                progress = startProgress,
                message = StrategyType.buildExecutingMessage(generationStep.strategy)
            )
        ))
        
        // 실시간 스트림으로 G 단계 실행
        val accumulatedResponse = StringBuilder()
        
        executeStepInternalStream(
            chat, generationStep, allRAResults, "", originalUserMessage, chatId, executionId, totalSteps, startProgress
        ).collect { chunk ->
            accumulatedResponse.append(chunk)
            
            // 진행률 계산: R+A가 있으면 78%~99%, 없으면 10%~99%
            val progressRange = if (ragSteps.retrievalAugmentationSteps.isNotEmpty()) 0.21 else 0.89
            val currentProgress = startProgress + (accumulatedResponse.length.toDouble() / 1000) * progressRange
            
            // 실시간 AI 응답 청크를 그대로 전달
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
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
                chatId = chatId,
                executionId = executionId,
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
        chat: Chat, 
        step: ActionStep, 
        optimizedContext: String, 
        dependencyResults: String,
        originalUserMessage: String,
        chatId: String,
        executionId: String,
        totalSteps: Int,
        currentProgress: Double
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
                ${if (dependencyResults.isNotBlank()) "\nDependencies: $dependencyResults" else ""}
                
                Provide a focused response for: ${step.expected_output}
                
                Make sure to directly address the original user question.
            """.trimIndent()
            
            // 실제 AI 스트림 호출 - 실시간 청크를 그대로 전달
            chatAssistant.callStream(
                chat.id.toString(), 
                strategy.createPrompt(stepPrompt), 
                strategy.getToolCallingChatOptions()
            ).collect { chatResponse ->
                val textChunk = chatResponse.result.output.text ?: ""
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
        chat: Chat, 
        step: ActionStep, 
        optimizedContext: String, 
        dependencyResults: String,
        originalUserMessage: String
    ): String {
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
                    ${if (dependencyResults.isNotBlank()) "\nDependencies: $dependencyResults" else ""}
                    
                    Provide a focused response for: ${step.expected_output}
                    
                    Make sure to directly address the original user question.
                """.trimIndent()
                
                // 일반 AI 호출 (결과만 필요)
                val response = chatAssistant.call(chat.id.toString(), strategy.createPrompt(stepPrompt), strategy.getToolCallingChatOptions())
                
                // 토큰 사용량 추적
                tokenUsageTracker.trackAndLogTokenUsage(response, "ReActStream 단계 ${step.step} - ${strategy.getType().id}")
                
                return@withContext response.result.output.text?.trim() 
                    ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
                    
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
        val retrievalAugmentationSteps = mutableListOf<ActionStep>()
        var generationStep: ActionStep? = null
        
        actions.forEach { step ->
            val strategyType = StrategyType.findById(step.strategy)
            when (strategyType?.executionType) {
                StrategyExecutionType.DATA_COLLECTION,
                StrategyExecutionType.DATA_PROCESSING,
                StrategyExecutionType.DECISION_MAKING -> {
                    retrievalAugmentationSteps.add(step)
                }
                StrategyExecutionType.RESPONSE_GENERATION -> {
                    generationStep = step
                }
                else -> {
                    logger.warn("Unknown strategy type for step ${step.step}: ${step.strategy}")
                    retrievalAugmentationSteps.add(step) // 기본적으로 R+A에 포함
                }
            }
        }
        
        return RAGSteps(
            retrievalAugmentationSteps = retrievalAugmentationSteps,
            generationStep = generationStep ?: throw IllegalStateException("No generation step found!")
        )
    }
    
    /**
     * R+A 단계들을 배치로 실행합니다.
     */
    private suspend fun executeRABatchSteps(
        chat: Chat,
        raSteps: List<ActionStep>,
        originalUserMessage: String,
        stepResults: ConcurrentHashMap<Int, String>,
        chatId: String,
        executionId: String,
        totalSteps: Int,
        emit: suspend (ReActStreamResponse) -> Unit
    ): Map<Int, String> {
        val results = mutableMapOf<Int, String>()
        
        // 의존성 순서대로 실행
        val sortedSteps = raSteps.sortedBy { it.step }
        
        for (step in sortedSteps) {
            try {
                logger.info("R+A 단계 ${step.step} 실행: ${step.strategy}")
                
                // 🔄 단계 실행 시작 알림
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
                        phase = ExecutionPhase.STEP_EXECUTING,
                        currentStep = step.step,
                        totalSteps = totalSteps,
                        progress = (results.size.toDouble() / raSteps.size) * 0.7, // R+A는 전체의 70%
                        message = StrategyType.buildExecutingMessage(step.strategy, step.purpose)
                    )
                ))
                
                // 의존성 결과 수집
                val dependencyResults = step.dependencies.mapNotNull { depStep ->
                    results[depStep]?.let { "Step $depStep result: $it" }
                }.joinToString("\n")
                
                // 컨텍스트 최적화 (Chat 객체 활용)
                val optimizedContext = contextOptimizer.buildOptimizedContextWithChat(chat, step, stepResults)
                
                // 단계 실행
                val stepResult = executeStepInternal(chat, step, optimizedContext, dependencyResults, originalUserMessage)
                results[step.step] = stepResult
                stepResults[step.step] = stepResult
                
                logger.info("R+A 단계 ${step.step} 완료")
                
                // ✅ 단계 완료 알림
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
                        phase = ExecutionPhase.STEP_COMPLETED,
                        currentStep = step.step,
                        totalSteps = totalSteps,
                        progress = ((results.size.toDouble() + 1) / raSteps.size) * 0.7,
                        message = StrategyType.buildCompletedMessage(step.strategy, step.purpose),
                        stepResult = stepResult.take(100) // 미리보기용 100자
                    )
                ))
                
            } catch (e: Exception) {
                logger.error("R+A 단계 ${step.step} 실패: ${e.message}", e)
                val errorResult = "단계 ${step.step} 실패: ${e.message}"
                results[step.step] = errorResult
                stepResults[step.step] = errorResult
                
                // ❌ 단계 실패 알림
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
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