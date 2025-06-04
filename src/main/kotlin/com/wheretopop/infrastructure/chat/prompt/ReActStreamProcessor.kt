package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import com.wheretopop.infrastructure.chat.AiChatAssistant
import com.wheretopop.infrastructure.chat.ChatAssistant
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyExecutionType
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * ReAct 스트림 처리를 담당하는 클래스
 */
@Component
class ReActStreamProcessor(
    private val multiStepExecutor: MultiStepExecutor,
    private val contextOptimizer: ContextOptimizer,
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>
) {
    private val logger = KotlinLogging.logger {}
    
    /**
     * 다단계 실행 계획을 스트림으로 실행합니다. (새로운 ChatStreamResponse 사용)
     */
    fun executeMultiStepPlanStreamV2(
        chat: Chat, 
        executionPlan: ReActResponse, 
        originalUserMessage: String,
        chatId: String,
        executionId: String
    ): Flow<ChatStreamResponse> {
        return multiStepExecutor.executeMultiStepPlanStream(
            chat, executionPlan, originalUserMessage, chatId, executionId
        )
    }
    
    /**
     * 다단계 실행 계획을 스트림으로 실행합니다. (기존 호환성 유지)
     */
    fun executeMultiStepPlanStream(
        chat: Chat, 
        executionPlan: ReActResponse, 
        originalUserMessage: String,
        chatId: String,
        executionId: String
    ): Flow<ReActStreamResponse> = flow {
        val stepResults = ConcurrentHashMap<Int, String>()
        val completedSteps = mutableSetOf<Int>()
        val totalSteps = executionPlan.actions.size
        
        logger.info("스트림 기반 다단계 실행 시작: 총 ${totalSteps}단계")
        
        // 단계별 의존성 그래프 생성
        val readySteps = mutableSetOf<ActionStep>()
        
        // 의존성이 없는 첫 번째 단계들 찾기
        executionPlan.actions.filter { it.dependencies.isEmpty() }.forEach { step ->
            readySteps.add(step)
        }
        
        while (readySteps.isNotEmpty() || completedSteps.size < totalSteps) {
            // 준비된 단계들을 병렬로 실행
            val currentBatch = readySteps.toList()
            readySteps.clear()
            
            if (currentBatch.isNotEmpty()) {
                logger.info("병렬 실행 배치: ${currentBatch.map { it.step }}")
                
                // 각 단계 실행 시작 알림
                currentBatch.forEach { step ->
                    emit(ReActStreamResponse(
                        status = ReActExecutionStatus(
                            chatId = chatId,
                            executionId = executionId,
                            phase = ExecutionPhase.STEP_EXECUTING,
                            currentStep = step.step,
                            totalSteps = totalSteps,
                            progress = completedSteps.size.toDouble() / totalSteps,
                            message = "단계 ${step.step} 실행 중: ${step.purpose}"
                        )
                    ))
                }
                
                // 병렬 실행
                val batchResults = executeBatchWithStreaming(
                    chat, currentBatch, originalUserMessage, stepResults, chatId, executionId, totalSteps, completedSteps
                )
                
                // 결과 수집 및 스트림 이벤트 발행
                batchResults.collect { (stepNum, result, isError) ->
                    stepResults[stepNum] = result
                    completedSteps.add(stepNum)
                    
                    val step = executionPlan.actions.find { it.step == stepNum }
                    
                    emit(ReActStreamResponse(
                        status = ReActExecutionStatus(
                            chatId = chatId,
                            executionId = executionId,
                            phase = if (isError) ExecutionPhase.STEP_FAILED else ExecutionPhase.STEP_COMPLETED,
                            currentStep = stepNum,
                            totalSteps = totalSteps,
                            progress = completedSteps.size.toDouble() / totalSteps,
                            message = if (isError) "단계 $stepNum 실패" else "단계 $stepNum 완료: ${step?.purpose ?: ""}",
                            stepResult = result.take(200), // 너무 긴 결과는 잘라서 전송
                            error = if (isError) result else null
                        )
                    ))
                }
                
                // 다음 실행 가능한 단계들 찾기
                executionPlan.actions.forEach { step ->
                    if (step.step !in completedSteps && 
                        step.dependencies.all { it in completedSteps }) {
                        readySteps.add(step)
                    }
                }
            } else {
                // 데드락 방지: 남은 단계가 있지만 실행 가능한 단계가 없는 경우
                val remainingSteps = executionPlan.actions.filter { it.step !in completedSteps }
                if (remainingSteps.isNotEmpty()) {
                    logger.warn("잠재적 데드락 감지. 순차 실행으로 전환.")
                    
                    // 순차 실행 스트림
                    executeSequentialWithStreaming(
                        chat, remainingSteps, originalUserMessage, stepResults, completedSteps, 
                        chatId, executionId, totalSteps
                    ).collect { streamResponse ->
                        emit(streamResponse)
                    }
                }
                break
            }
        }
        
        // 결과 통합 시작
        emit(ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chatId,
                executionId = executionId,
                phase = ExecutionPhase.AGGREGATING,
                currentStep = null,
                totalSteps = totalSteps,
                progress = 0.95,
                message = "결과를 통합하고 있습니다..."
            )
        ))
        
        // 최종 결과 생성
        val finalResult = generateFinalResult(executionPlan, stepResults)
        
        // 응답 생성 시작 알림
        emit(ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chatId,
                executionId = executionId,
                phase = ExecutionPhase.AGGREGATING,
                currentStep = null,
                totalSteps = totalSteps,
                progress = 0.98,
                message = "최종 응답을 생성하고 있습니다..."
            )
        ))
        
        // 최종 응답을 AI 스트림으로 생성
        val responseGenerationStep = executionPlan.actions.find { 
            StrategyType.findById(it.strategy)?.executionType == StrategyExecutionType.RESPONSE_GENERATION 
        }
        
        if (responseGenerationStep != null) {
            // 응답 생성 전략이 있는 경우 실시간 스트림으로 생성
            val allStepResults = stepResults.entries.joinToString("\n") { (stepNum, result) ->
                "Step $stepNum: $result"
            }
            
            executeStepInternalStream(
                chat, responseGenerationStep, allStepResults, "", chatId, executionId, totalSteps, 0.98
            ).collect { chunk ->
                // 실시간 AI 응답 청크를 그대로 전달
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
                        phase = ExecutionPhase.AGGREGATING,
                        currentStep = null,
                        totalSteps = totalSteps,
                        progress = 0.99,
                        message = "응답 생성 중..."
                    ),
                    isComplete = false,
                    finalResult = chunk // 실시간 AI 청크
                ))
            }
        } else {
            // 응답 생성 전략이 없는 경우 마지막 단계를 스트림으로 재실행
            val lastStep = executionPlan.actions.maxByOrNull { it.step }
            if (lastStep != null) {
                // 모든 이전 단계 결과를 컨텍스트로 사용
                val allStepResults = stepResults.entries.filter { it.key != lastStep.step }
                    .joinToString("\n") { (stepNum, result) -> "Step $stepNum: $result" }
                
                // 마지막 단계를 스트림으로 재실행
                executeStepInternalStream(
                    chat, lastStep, allStepResults, "", chatId, executionId, totalSteps, 0.98
                ).collect { chunk ->
                    // 실시간 AI 응답 청크를 그대로 전달
                    emit(ReActStreamResponse(
                        status = ReActExecutionStatus(
                            chatId = chatId,
                            executionId = executionId,
                            phase = ExecutionPhase.AGGREGATING,
                            currentStep = null,
                            totalSteps = totalSteps,
                            progress = 0.99,
                            message = "최종 응답 생성 중..."
                        ),
                        isComplete = false,
                        finalResult = chunk // 실시간 AI 청크
                    ))
                }
            } else {
                // 폴백: 기존 방식 (글자별 스트림)
                finalResult.chunked(1).forEachIndexed { index, chunk ->
                    delay(30) // 타이핑 효과
                    emit(ReActStreamResponse(
                        status = ReActExecutionStatus(
                            chatId = chatId,
                            executionId = executionId,
                            phase = ExecutionPhase.AGGREGATING,
                            currentStep = null,
                            totalSteps = totalSteps,
                            progress = 0.98 + (index.toDouble() / finalResult.length) * 0.02,
                            message = "응답 생성 중..."
                        ),
                        isComplete = false,
                        finalResult = chunk // 글자별로 전송
                    ))
                }
            }
        }
        
        // 최종 완료
        emit(ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chatId,
                executionId = executionId,
                phase = ExecutionPhase.COMPLETED,
                currentStep = null,
                totalSteps = totalSteps,
                progress = 1.0,
                message = "모든 단계가 완료되었습니다"
            ),
            isComplete = true,
            finalResult = null // 전체 결과는 이미 청크로 전송했으므로 null
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
            
            // 최적화된 프롬프트 생성
            val stepPrompt = """
                Execute: ${step.purpose}
                
                Context: $optimizedContext
                ${if (dependencyResults.isNotBlank()) "\nDependencies: $dependencyResults" else ""}
                
                Provide a focused response for: ${step.expected_output}
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
     * 배치를 스트림과 함께 병렬 실행합니다.
     */
    private fun executeBatchWithStreaming(
        chat: Chat,
        batch: List<ActionStep>,
        originalUserMessage: String,
        stepResults: ConcurrentHashMap<Int, String>,
        chatId: String,
        executionId: String,
        totalSteps: Int,
        completedSteps: Set<Int>
    ): Flow<Triple<Int, String, Boolean>> = flow {
        val results = runBlocking {
            batch.map { step ->
                async(Dispatchers.IO) {
                    try {
                        logger.info("단계 ${step.step} 실행: ${step.strategy}")
                        
                        // 의존성 결과 수집
                        val dependencyResults = step.dependencies.mapNotNull { depStep ->
                            stepResults[depStep]?.let { "Step $depStep result: $it" }
                        }.joinToString("\n")
                        
                        // 컨텍스트 최적화
                        val optimizedContext = contextOptimizer.buildOptimizedContext(originalUserMessage, step, stepResults)
                        
                        // 단계별 실행 (일반 버전 - 결과만 필요)
                        val stepResult = executeStepInternal(chat, step, optimizedContext, dependencyResults)
                        
                        logger.info("단계 ${step.step} 완료")
                        Triple(step.step, stepResult, false)
                        
                    } catch (e: Exception) {
                        logger.error("단계 ${step.step} 실패: ${e.message}", e)
                        Triple(step.step, "단계 ${step.step} 실패: ${e.message}", true)
                    }
                }
            }.awaitAll()
        }
        
        results.forEach { emit(it) }
    }
    
    /**
     * 순차 실행을 스트림과 함께 진행합니다.
     */
    private fun executeSequentialWithStreaming(
        chat: Chat,
        remainingSteps: List<ActionStep>,
        originalUserMessage: String,
        stepResults: ConcurrentHashMap<Int, String>,
        completedSteps: MutableSet<Int>,
        chatId: String,
        executionId: String,
        totalSteps: Int
    ): Flow<ReActStreamResponse> = flow {
        remainingSteps.forEach { step ->
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.STEP_EXECUTING,
                    currentStep = step.step,
                    totalSteps = totalSteps,
                    progress = completedSteps.size.toDouble() / totalSteps,
                    message = "단계 ${step.step} 순차 실행 중: ${step.purpose}"
                )
            ))
            
            try {
                val dependencyResults = step.dependencies.mapNotNull { depStep ->
                    stepResults[depStep]?.let { "Step $depStep result: $it" }
                }.joinToString("\n")
                
                val optimizedContext = contextOptimizer.buildOptimizedContext(originalUserMessage, step, stepResults)
                val stepResult = executeStepInternal(chat, step, optimizedContext, dependencyResults)
                
                stepResults[step.step] = stepResult
                completedSteps.add(step.step)
                
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
                        phase = ExecutionPhase.STEP_COMPLETED,
                        currentStep = step.step,
                        totalSteps = totalSteps,
                        progress = completedSteps.size.toDouble() / totalSteps,
                        message = "단계 ${step.step} 완료: ${step.purpose}",
                        stepResult = stepResult.take(200)
                    )
                ))
            } catch (e: Exception) {
                logger.error("단계 ${step.step} 실패: ${e.message}", e)
                val errorResult = "단계 ${step.step} 실패: ${e.message}"
                stepResults[step.step] = errorResult
                completedSteps.add(step.step)
                
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
                        phase = ExecutionPhase.STEP_FAILED,
                        currentStep = step.step,
                        totalSteps = totalSteps,
                        progress = completedSteps.size.toDouble() / totalSteps,
                        message = "단계 ${step.step} 실패",
                        error = errorResult
                    )
                ))
            }
        }
    }
    
    /**
     * 개별 단계 실행 (일반 버전) - 결과만 필요한 경우
     */
    private suspend fun executeStepInternal(
        chat: Chat, 
        step: ActionStep, 
        optimizedContext: String, 
        dependencyResults: String
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                val strategyType = StrategyType.findById(step.strategy)
                    ?: throw IllegalArgumentException("Unknown strategy: ${step.strategy}")
                
                val strategy = strategies.find { it.getType() == strategyType }
                    ?: throw IllegalStateException("No strategy found for type: ${strategyType.id}")
                
                // 최적화된 프롬프트 생성
                val stepPrompt = """
                    Execute: ${step.purpose}
                    
                    Context: $optimizedContext
                    ${if (dependencyResults.isNotBlank()) "\nDependencies: $dependencyResults" else ""}
                    
                    Provide a focused response for: ${step.expected_output}
                """.trimIndent()
                
                // 일반 AI 호출 (결과만 필요)
                val response = chatAssistant.call(chat.id.toString(), strategy.createPrompt(stepPrompt), strategy.getToolCallingChatOptions())
                return@withContext response.result.output.text?.trim() 
                    ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
                    
            } catch (e: Exception) {
                logger.error("Step ${step.step} execution failed", e)
                throw e
            }
        }
    }
    
    /**
     * 최종 결과를 생성합니다.
     */
    private fun generateFinalResult(executionPlan: ReActResponse, stepResults: ConcurrentHashMap<Int, String>): String {
        val responseGenerationStep = executionPlan.actions.find { 
            StrategyType.findById(it.strategy)?.executionType == StrategyExecutionType.RESPONSE_GENERATION 
        }
        
        return if (responseGenerationStep != null) {
            stepResults[responseGenerationStep.step] ?: "응답 생성 결과를 찾을 수 없습니다"
        } else {
            val lastStep = executionPlan.actions.maxByOrNull { it.step }
            stepResults[lastStep?.step ?: 1] ?: "결과를 생성할 수 없습니다"
        }
    }
}