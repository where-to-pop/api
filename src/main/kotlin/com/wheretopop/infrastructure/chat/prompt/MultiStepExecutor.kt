package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import com.wheretopop.infrastructure.chat.ChatAssistant
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyExecutionType
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * 다단계 실행을 담당하는 클래스 (병렬/순차 실행 관리)
 */
@Component
class MultiStepExecutor(
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>,
    private val contextOptimizer: ContextOptimizer,
    private val executionPlanOptimizer: ExecutionPlanOptimizer,
    private val performanceMonitor: PerformanceMonitor,
    private val tokenUsageTracker: TokenUsageTracker
) {
    private val logger = KotlinLogging.logger {}
    
    /**
     * 최적화된 다단계 실행 계획을 병렬로 실행합니다.
     */
    suspend fun executeMultiStepPlan(
        chat: Chat, 
        executionPlan: ReActResponse, 
        originalUserMessage: String
    ): String {
        val optimizedPlan = executionPlanOptimizer.optimizeExecutionPlan(executionPlan)
        val stepResults = ConcurrentHashMap<Int, String>()
        val completedSteps = mutableSetOf<Int>()
        
        logger.info("Starting optimized multi-step execution with ${optimizedPlan.actions.size} steps")
        
        // 단계별 의존성 그래프 생성
        val readySteps = mutableSetOf<ActionStep>()
        
        // 의존성이 없는 첫 번째 단계들 찾기
        optimizedPlan.actions.filter { it.dependencies.isEmpty() }.forEach { step ->
            readySteps.add(step)
        }
        
        while (readySteps.isNotEmpty() || completedSteps.size < optimizedPlan.actions.size) {
            // 준비된 단계들을 병렬로 실행
            val currentBatch = readySteps.toList()
            readySteps.clear()
            
            if (currentBatch.isNotEmpty()) {
                logger.info("Executing batch of ${currentBatch.size} parallel steps: ${currentBatch.map { it.step }}")
                
                val batchResults = performanceMonitor.measureTimeAsync("Parallel batch execution") {
                    executeBatchInParallel(chat, currentBatch, originalUserMessage, stepResults)
                }
                
                // 결과 저장 및 완료된 단계 추가
                batchResults.forEach { (stepNum, result) ->
                    stepResults[stepNum] = result
                    completedSteps.add(stepNum)
                }
                
                // 다음 실행 가능한 단계들 찾기
                optimizedPlan.actions.forEach { step ->
                    if (step.step !in completedSteps && 
                        step.dependencies.all { it in completedSteps }) {
                        readySteps.add(step)
                    }
                }
            } else {
                // 데드락 방지: 남은 단계가 있지만 실행 가능한 단계가 없는 경우
                val remainingSteps = optimizedPlan.actions.filter { it.step !in completedSteps }
                if (remainingSteps.isNotEmpty()) {
                    logger.warn("Potential deadlock detected. Executing remaining steps sequentially.")
                    executeRemainingStepsSequentially(chat, remainingSteps, originalUserMessage, stepResults, completedSteps)
                }
                break
            }
        }
        
        // 최종 결과 반환
        return getFinalResult(optimizedPlan, stepResults)
    }
    
    /**
     * 스트림과 함께 다단계 실행을 진행합니다.
     */
    fun executeMultiStepPlanStream(
        chat: Chat,
        executionPlan: ReActResponse,
        originalUserMessage: String,
        chatId: String,
        executionId: String
    ): Flow<ChatStreamResponse> = flow {
        val stepResults = ConcurrentHashMap<Int, String>()
        val completedSteps = mutableSetOf<Int>()
        
        try {
            // 단계별 의존성 그래프 생성
            val readySteps = mutableSetOf<ActionStep>()
            executionPlan.actions.filter { it.dependencies.isEmpty() }.forEach { step ->
                readySteps.add(step)
            }
            
            while (readySteps.isNotEmpty() || completedSteps.size < executionPlan.actions.size) {
                val currentBatch = readySteps.toList()
                readySteps.clear()
                
                if (currentBatch.isNotEmpty()) {
                    // 각 단계별 실행 스트림
                    for (step in currentBatch) {
                        // 도구 실행 시작 알림
                        emit(ChatStreamResponse(
                            chatId = chatId,
                            executionId = executionId,
                            type = StreamMessageType.TOOL_EXECUTING,
                            currentStep = step.step,
                            totalSteps = executionPlan.actions.size,
                            progress = completedSteps.size.toDouble() / executionPlan.actions.size,
                            toolName = step.strategy,
                            actionDescription = "${step.purpose} 실행 중..."
                        ))
                        
                        // 구체적인 진행 메시지들
                        val progressMessages = getProgressMessages(step)
                        progressMessages.forEach { message ->
                            delay(300)
                            emit(ChatStreamResponse(
                                chatId = chatId,
                                executionId = executionId,
                                type = StreamMessageType.TOOL_EXECUTING,
                                currentStep = step.step,
                                totalSteps = executionPlan.actions.size,
                                actionDescription = message
                            ))
                        }
                        
                        // 실제 단계 실행
                        val stepResult = executeStepWithProgress(chat, step, originalUserMessage, stepResults)
                        stepResults[step.step] = stepResult
                        completedSteps.add(step.step)
                        
                        // 완료 알림
                        emit(ChatStreamResponse(
                            chatId = chatId,
                            executionId = executionId,
                            type = StreamMessageType.STATUS_UPDATE,
                            currentStep = step.step,
                            totalSteps = executionPlan.actions.size,
                            progress = completedSteps.size.toDouble() / executionPlan.actions.size,
                            statusMessage = "단계 ${step.step} 완료: ${step.purpose}"
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
                    break
                }
            }
            
            // 최종 응답 생성 시작
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.RESPONSE_GENERATING,
                progress = 0.9,
                statusMessage = "최종 응답을 생성하고 있습니다..."
            ))
            
            val finalResult = getFinalResult(executionPlan, stepResults)
            
            // 응답을 글자별로 스트림
            finalResult.chunked(1).forEachIndexed { index, chunk ->
                delay(30) // 타이핑 효과
                emit(ChatStreamResponse(
                    chatId = chatId,
                    executionId = executionId,
                    type = StreamMessageType.RESPONSE_CHUNK,
                    responseChunk = chunk,
                    progress = 0.9 + (index.toDouble() / finalResult.length) * 0.1
                ))
            }
            
            // 완료
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.COMPLETED,
                isComplete = true,
                finalResponse = finalResult,
                progress = 1.0,
                statusMessage = "응답 생성이 완료되었습니다"
            ))
            
        } catch (e: Exception) {
            logger.error("Multi-step execution stream failed", e)
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.ERROR,
                errorMessage = "실행 중 오류가 발생했습니다: ${e.message}",
                errorCode = "EXECUTION_ERROR"
            ))
        }
    }
    
    /**
     * 단계별 진행 메시지를 생성합니다.
     */
    private fun getProgressMessages(step: ActionStep): List<String> {
        return when (StrategyType.findById(step.strategy)) {
            StrategyType.AREA_QUERY -> listOf(
                "지역 정보를 조회하고 있습니다...",
                "혼잡도 데이터를 분석하고 있습니다...",
                "인구 통계를 확인하고 있습니다..."
            )
            StrategyType.BUILDING_QUERY -> listOf(
                "건물 정보를 검색하고 있습니다...",
                "시설 데이터를 수집하고 있습니다...",
                "상세 정보를 조회하고 있습니다..."
            )
            StrategyType.POPUP_QUERY -> listOf(
                "팝업스토어 정보를 조회하고 있습니다...",
                "이벤트 데이터를 확인하고 있습니다..."
            )
            StrategyType.ONLINE_SEARCH -> listOf(
                "온라인에서 관련 정보를 검색하고 있습니다...",
                "최신 데이터를 수집하고 있습니다...",
                "검색 결과를 분석하고 있습니다..."
            )
            else -> listOf(
                "${step.purpose} 처리 중...",
                "데이터를 분석하고 있습니다..."
            )
        }
    }
    
    /**
     * 진행 상황과 함께 단계를 실행합니다.
     */
    private suspend fun executeStepWithProgress(
        chat: Chat,
        step: ActionStep,
        originalUserMessage: String,
        stepResults: ConcurrentHashMap<Int, String>
    ): String {
        // 의존성 결과 수집
        val dependencyResults = step.dependencies.mapNotNull { depStep ->
            stepResults[depStep]?.let { "Step $depStep result: $it" }
        }.joinToString("\n")
        
        // 컨텍스트 최적화
        val optimizedContext = contextOptimizer.buildOptimizedContext(originalUserMessage, step, stepResults)
        
        // 단계별 실행
        return executeStep(chat, step, optimizedContext, dependencyResults)
    }
    
    /**
     * 배치를 병렬로 실행합니다.
     */
    private suspend fun executeBatchInParallel(
        chat: Chat,
        batch: List<ActionStep>,
        originalUserMessage: String,
        stepResults: ConcurrentHashMap<Int, String>
    ): List<Pair<Int, String>> {
        return coroutineScope { batch.map { step ->
            async(Dispatchers.IO) {
                try {
                    logger.info("Executing Step ${step.step}: ${step.strategy}")
                    
                    // 의존성 결과 수집
                    val dependencyResults = step.dependencies.mapNotNull { depStep ->
                        stepResults[depStep]?.let { "Step $depStep result: $it" }
                    }.joinToString("\n")
                    
                    // 컨텍스트 최적화: 필요한 정보만 포함
                    val optimizedContext = contextOptimizer.buildOptimizedContext(originalUserMessage, step, stepResults)
                    
                    // 단계별 실행
                    val stepResult = executeStep(chat, step, optimizedContext, dependencyResults)
                    
                    logger.info("Step ${step.step} completed successfully")
                    step.step to stepResult
                    
                } catch (e: Exception) {
                    logger.error("Step ${step.step} failed: ${e.message}", e)
                    step.step to "Step ${step.step} failed: ${e.message}"
                }
            }
        }.awaitAll()
        }
    }
    
    /**
     * 남은 단계들을 순차적으로 실행합니다.
     */
    private fun executeRemainingStepsSequentially(
        chat: Chat,
        remainingSteps: List<ActionStep>,
        originalUserMessage: String,
        stepResults: ConcurrentHashMap<Int, String>,
        completedSteps: MutableSet<Int>
    ) {
        remainingSteps.forEach { step ->
            try {
                val dependencyResults = step.dependencies.mapNotNull { depStep ->
                    stepResults[depStep]?.let { "Step $depStep result: $it" }
                }.joinToString("\n")
                
                val optimizedContext = contextOptimizer.buildOptimizedContext(originalUserMessage, step, stepResults)
                val stepResult = executeStep(chat, step, optimizedContext, dependencyResults)
                
                stepResults[step.step] = stepResult
                completedSteps.add(step.step)
            } catch (e: Exception) {
                logger.error("Step ${step.step} failed: ${e.message}", e)
                stepResults[step.step] = "Step ${step.step} failed: ${e.message}"
                completedSteps.add(step.step)
            }
        }
    }
    
    /**
     * 개별 단계를 실행합니다.
     */
    private fun executeStep(chat: Chat, step: ActionStep, optimizedContext: String, dependencyResults: String): String {
        val strategyType = StrategyType.findById(step.strategy)
            ?: throw IllegalArgumentException("Unknown strategy: ${step.strategy}")
        
        val strategy = getStrategyByType(strategyType)
        
        // 최적화된 프롬프트 생성 (간결하게)
        val stepPrompt = """
            Execute: ${step.purpose}
            
            Context: $optimizedContext
            ${if (dependencyResults.isNotBlank()) "\nDependencies: $dependencyResults" else ""}
            
            Provide a focused response for: ${step.expected_output}
        """.trimIndent()
        
        val response = executeStrategy(chat.id.toString(), strategy, stepPrompt)
        return response.result.output.text?.trim() 
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }
    
    /**
     * 최종 결과를 생성합니다.
     */
    private fun getFinalResult(executionPlan: ReActResponse, stepResults: ConcurrentHashMap<Int, String>): String {
        val responseGenerationStep = executionPlan.actions.find { 
            StrategyType.findById(it.strategy)?.executionType == StrategyExecutionType.RESPONSE_GENERATION 
        }
        
        return if (responseGenerationStep != null) {
            stepResults[responseGenerationStep.step] ?: "No response generation result available"
        } else {
            // 응답 생성 전략이 없는 경우 마지막 단계 결과 반환
            val lastStep = executionPlan.actions.maxByOrNull { it.step }
            stepResults[lastStep?.step ?: 1] ?: "No results available"
        }
    }
    
    private fun getStrategyByType(type: StrategyType): ChatPromptStrategy {
        return strategies.find { it.getType() == type }
            ?: throw IllegalStateException("No strategy found for type: ${type.id}")
    }
    
    private fun executeStrategy(conversationId: String, strategy: ChatPromptStrategy, userMessage: String): org.springframework.ai.chat.model.ChatResponse {
        val response = chatAssistant.call(conversationId, strategy.createPrompt(userMessage), strategy.getToolCallingChatOptions())
        
        // 토큰 사용량 추적
        tokenUsageTracker.trackAndLogTokenUsage(response, "MultiStep 실행 - ${strategy.getType().id}")
        
        return response
    }
} 