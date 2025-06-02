package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatMessage
import com.wheretopop.domain.chat.ChatScenario
import com.wheretopop.infrastructure.chat.ChatAssistant
import com.wheretopop.shared.enums.ChatMessageRole
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import mu.KotlinLogging
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.stereotype.Component
import java.time.Instant
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyExecutionType
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * ReAct strategy selection response parsing data classes for multi-step execution
 */
data class ReActResponse(
    val thought: String,
    val actions: List<ActionStep>,
    val observation: String
)

data class ActionStep(
    val step: Int,
    val strategy: String,
    val purpose: String,
    val reasoning: String,
    val recommended_tools: List<String>,
    val tool_sequence: String,
    val expected_output: String,
    val dependencies: List<Int> = emptyList() // 이전 단계 의존성
)

data class ExecutionPlan(
    val total_steps: Int,
    val execution_strategy: String,
    val final_goal: String
)

/**
 * ReAct 실행 상태 및 스트림 응답을 위한 데이터 클래스들
 */
data class ReActExecutionStatus(
    val chatId: String,
    val executionId: String,
    val phase: ExecutionPhase,
    val currentStep: Int?,
    val totalSteps: Int,
    val progress: Double, // 0.0 ~ 1.0
    val message: String,
    val stepResult: String? = null,
    val error: String? = null,
    val timestamp: Instant = Instant.now()
)

enum class ExecutionPhase {
    PLANNING,           // 실행 계획 생성 중
    STEP_EXECUTING,     // 개별 단계 실행 중
    STEP_COMPLETED,     // 개별 단계 완료
    STEP_FAILED,        // 개별 단계 실패
    AGGREGATING,        // 결과 통합 중
    COMPLETED,          // 전체 실행 완료
    FAILED              // 전체 실행 실패
}

data class ReActStreamResponse(
    val status: ReActExecutionStatus,
    val isComplete: Boolean = false,
    val finalResult: String? = null
)

/**
 * 사용자 메시지에 따라 적절한 전략을 선택하고 실행하는 관리자 클래스
 * ReAct 패턴을 지원하여 더 지능적인 전략 선택을 수행합니다.
 * 성능 최적화를 위한 병렬 처리, 캐싱, 조기 종료 등을 지원합니다.
 */
@Component
class ChatPromptStrategyManager(
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>
): ChatScenario {
    private val logger = KotlinLogging.logger {}
    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        // ISO 8601 형식으로 날짜 직렬화
        disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    
    // 실행 계획 캐시 (동일한 쿼리 패턴에 대한 최적화)
    private val executionPlanCache = ConcurrentHashMap<String, ReActResponse>()
    
    // 성능 모니터링
    private fun <T> measureTime(operation: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        return try {
            block()
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logger.info("$operation completed in ${duration}ms")
        }
    }
    
    /**
     * 단순한 쿼리인지 판단합니다 (다단계 실행이 불필요한 경우)
     */
    private fun isSimpleQuery(userMessage: String): Boolean {
        val simplePatterns = listOf(
            "안녕", "hello", "hi", "감사", "고마워", "도움말", "help"
        )
        return simplePatterns.any { userMessage.contains(it, ignoreCase = true) } ||
               userMessage.length < 10
    }
    
    /**
     * 사용자 메시지를 기반으로 채팅 제목을 생성합니다.
     * 
     * @param userMessage 사용자의 첫 메시지
     * @return 생성된 채팅 제목
     */
    override fun generateTitle(chat: Chat): String {
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        logger.info("Generating chat title for message: $userMessage")
        val titleStrategy = getStrategyByType(StrategyType.TITLE_GENERATION)
        val response = executeStrategy(chat.id.toString(), titleStrategy, userMessage)
        
        // 응답에서 제목만 추출
        return response.result.output.text?.trim()
            ?.let { if (it.length > 50) it.take(47) + "…" else it }
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException();
    }
    
    /**
     * 사용자 메시지를 처리하고 적절한 응답을 생성합니다.
     * ReAct 다단계 실행 계획을 순차적으로 실행합니다.
     * 
     * @param chat 채팅 객체
     * @return AI 응답이 추가된 채팅 객체
     */
    override fun processUserMessage(chat: Chat): Chat = measureTime("Total message processing") {
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()

        // 단순한 쿼리는 직접 처리 (다단계 실행 생략)
        if (isSimpleQuery(userMessage)) {
            logger.info("Processing simple query directly")
            return@measureTime processSimpleQuery(chat, userMessage)
        }

        // 캐시된 실행 계획 확인
        val cacheKey = generateAdvancedCacheKey(userMessage)
        val executionPlan = executionPlanCache[cacheKey] ?: run {
            val plan = measureTime("Execution plan creation") { createExecutionPlan(chat) }
            val optimizedPlan = optimizeExecutionPlan(plan)
            executionPlanCache[cacheKey] = optimizedPlan
            optimizedPlan
        }
        
        // 최적화된 다단계 실행 (병렬 처리)
        val finalResponse = measureTime("Multi-step execution") { 
            executeMultiStepPlanOptimized(chat, executionPlan, userMessage) 
        }

        logger.info("Final AI response length: ${finalResponse.length} characters")
        chat.addMessage(ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.ASSISTANT,
            content = finalResponse,
            finishReason = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        ))
    }
    
    /**
     * 단순한 쿼리를 직접 처리합니다.
     */
    private fun processSimpleQuery(chat: Chat, userMessage: String): Chat {
        val strategy = getStrategyByType(StrategyType.GENERAL_RESPONSE) // 폴백 전략 사용
        val response = executeStrategy(chat.id.toString(), strategy, userMessage)
        val responseText = response.result.output.text?.trim() 
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        
        return chat.addMessage(ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.ASSISTANT,
            content = responseText,
            finishReason = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        ))
    }
    
    /**
     * ReAct 실행 계획을 생성합니다.
     * 
     * @param chat 채팅 객체
     * @return ReAct 응답 객체
     */
    private fun createExecutionPlan(chat: Chat): ReActResponse {
        val reActPlanner = getStrategyByType(StrategyType.REACT_PLANNER)
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        
        val response = executeStrategy(chat.id.toString(), reActPlanner, userMessage)
        val responseText = response.result.output.text?.trim() 
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        
        logger.info("ReAct execution planning response: $responseText")
        
        return try {
            val reActResponse = parseReActResponse(responseText)
            
            // 실행 계획 로깅
            logger.info("ReAct Multi-Step Execution Plan:")
            logger.info("- Thought: ${reActResponse.thought}")
            logger.info("- Total Steps: ${reActResponse.actions.size}")
            
            reActResponse.actions.forEachIndexed { _, step ->
                logger.info("  Step ${step.step}:")
                logger.info("    - Strategy: ${step.strategy}")
                logger.info("    - Purpose: ${step.purpose}")
                logger.info("    - Reasoning: ${step.reasoning}")
                logger.info("    - Tools: ${step.recommended_tools}")
                logger.info("    - Expected Output: ${step.expected_output}")
                logger.info("    - Dependencies: ${step.dependencies}")
            }
            
            logger.info("- Observation: ${reActResponse.observation}")
            
            reActResponse
        } catch (e: Exception) {
            logger.error("Failed to parse ReAct response: $responseText", e)
            
            // 폴백: 단일 전략 실행 계획 생성
            val fallbackStrategyId = extractStrategyIdFallback(responseText)
            createFallbackExecutionPlan(fallbackStrategyId)
        }
    }
    
    /**
     * 다단계 실행 계획을 순차적으로 실행합니다.
     * 
     * @param chat 채팅 객체
     * @param executionPlan 실행 계획
     * @param originalUserMessage 원본 사용자 메시지
     * @return 최종 응답
     */
    private fun executeMultiStepPlan(chat: Chat, executionPlan: ReActResponse, originalUserMessage: String): String {
        val stepResults = mutableMapOf<Int, String>()
        var accumulatedContext = "Original user query: $originalUserMessage\n\n"
        
        logger.info("Starting multi-step execution with ${executionPlan.actions.size} steps")
        
        for (step in executionPlan.actions.sortedBy { it.step }) {
            logger.info("Executing Step ${step.step}: ${step.strategy}")
            
            try {
                // 의존성 확인
                val dependencyResults = step.dependencies.mapNotNull { depStep ->
                    stepResults[depStep]?.let { "Step $depStep result: $it" }
                }.joinToString("\n")
                
                // 단계별 실행
                val stepResult = executeStep(chat, step, accumulatedContext, dependencyResults)
                stepResults[step.step] = stepResult
                
                // 컨텍스트 누적
                accumulatedContext += "Step ${step.step} (${step.strategy}) result: $stepResult\n\n"
                
                logger.info("Step ${step.step} completed successfully")
                
            } catch (e: Exception) {
                logger.error("Step ${step.step} failed: ${e.message}", e)
                
                // 실패한 단계가 있어도 계속 진행 (부분적 결과라도 제공)
                val errorResult = "Step ${step.step} failed: ${e.message}"
                stepResults[step.step] = errorResult
                accumulatedContext += "$errorResult\n\n"
            }
        }
        
        // 최종 결과 반환 (응답 생성 전략이 있으면 그 결과, 아니면 마지막 단계 결과)
        val responseGenerationStep = executionPlan.actions.find { 
            StrategyType.findById(it.strategy)?.executionType == StrategyExecutionType.RESPONSE_GENERATION 
        }
        
        return if (responseGenerationStep != null) {
            stepResults[responseGenerationStep.step] ?: accumulatedContext
        } else {
            // 응답 생성 전략이 없는 경우 마지막 단계 결과 반환
            val lastStep = executionPlan.actions.maxByOrNull { it.step }
            stepResults[lastStep?.step ?: 1] ?: "No results available"
        }
    }
    
    /**
     * 개별 단계를 실행합니다.
     * 
     * @param chat 채팅 객체
     * @param step 실행할 단계
     * @param accumulatedContext 누적된 컨텍스트
     * @param dependencyResults 의존성 단계 결과
     * @return 단계 실행 결과
     */
    private fun executeStep(chat: Chat, step: ActionStep, accumulatedContext: String, dependencyResults: String): String {
        val strategyType = StrategyType.findById(step.strategy)
            ?: throw IllegalArgumentException("Unknown strategy: ${step.strategy}")
        
        val strategy = getStrategyByType(strategyType)
        
        // 단계별 프롬프트 생성 (컨텍스트와 의존성 결과 포함)
        val stepPrompt = buildStepPrompt(step, accumulatedContext, dependencyResults)
        
        val response = executeStrategy(chat.id.toString(), strategy, stepPrompt)
        return response.result.output.text?.trim() 
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }
    
    /**
     * 단계별 실행을 위한 프롬프트를 생성합니다.
     * 
     * @param step 실행할 단계
     * @param accumulatedContext 누적된 컨텍스트
     * @param dependencyResults 의존성 결과
     * @return 단계별 프롬프트
     */
    private fun buildStepPrompt(step: ActionStep, accumulatedContext: String, dependencyResults: String): String {
        return """
            You are executing Step ${step.step} of a multi-step plan.
            
            Step Purpose: ${step.purpose}
            Step Strategy: ${step.strategy}
            Expected Output: ${step.expected_output}
            
            Context from previous steps:
            $accumulatedContext
            
            ${if (dependencyResults.isNotBlank()) "Dependency results:\n$dependencyResults\n" else ""}
            
            Please execute this step according to your strategy guidelines and produce the expected output.
            Focus specifically on: ${step.purpose}
        """.trimIndent()
    }
    
    /**
     * 폴백 실행 계획을 생성합니다.
     * 
     * @param strategyId 폴백 전략 ID
     * @return 폴백 실행 계획
     */
    private fun createFallbackExecutionPlan(strategyId: String): ReActResponse {
        return ReActResponse(
            thought = "Fallback execution plan due to parsing failure",
            actions = listOf(
                ActionStep(
                    step = 1,
                    strategy = strategyId,
                    purpose = "Execute fallback strategy",
                    reasoning = "Using fallback strategy due to plan parsing failure",
                    recommended_tools = emptyList(),
                    tool_sequence = "Direct strategy execution",
                    expected_output = "Strategy-specific response",
                    dependencies = emptyList()
                )
            ),
            observation = "Fallback plan created for single strategy execution"
        )
    }
    
    /**
     * ReAct 응답을 파싱합니다.
     * 
     * @param responseText AI 응답 텍스트
     * @return 파싱된 ReAct 응답
     */
    private fun parseReActResponse(responseText: String): ReActResponse {
        // JSON 블록 추출 (```json ... ``` 형태)
        val jsonPattern = Regex("```json\\s*([\\s\\S]*?)\\s*```")
        val jsonMatch = jsonPattern.find(responseText)
        
        val jsonText = jsonMatch?.groupValues?.get(1) ?: responseText
        
        return objectMapper.readValue<ReActResponse>(jsonText)
    }
    
    /**
     * JSON 파싱 실패 시 사용하는 폴백 전략 ID 추출 메서드
     * 
     * @param responseText AI 응답 텍스트
     * @return 추출된 전략 ID
     */
    private fun extractStrategyIdFallback(responseText: String): String {
        // 전략 ID 패턴 매칭 시도
        val strategyIds = StrategyType.entries.map { it.id }
        
        for (strategyId in strategyIds) {
            if (responseText.contains(strategyId, ignoreCase = true)) {
                return strategyId
            }
        }
        
        // 키워드 기반 추론
        return when {
            responseText.contains("지역", ignoreCase = true) || 
            responseText.contains("area", ignoreCase = true) ||
            responseText.contains("혼잡", ignoreCase = true) -> StrategyType.AREA_QUERY.id
            
            responseText.contains("건물", ignoreCase = true) || 
            responseText.contains("building", ignoreCase = true) -> StrategyType.BUILDING_QUERY.id
            
            responseText.contains("팝업", ignoreCase = true) || 
            responseText.contains("popup", ignoreCase = true) -> StrategyType.POPUP_QUERY.id
            
            responseText.contains("검색", ignoreCase = true) || 
            responseText.contains("search", ignoreCase = true) ||
            responseText.contains("온라인", ignoreCase = true) -> StrategyType.ONLINE_SEARCH.id
            
            else -> StrategyType.GENERAL_RESPONSE.id // 폴백을 GENERAL_RESPONSE로 변경
        }
    }
    
    /**
     * 주어진 전략 타입의 전략을 찾아 반환합니다.
     * 
     * @param type 전략 타입
     * @return 해당 타입의 전략
     * @throws IllegalStateException 해당 타입의 전략이 없는 경우
     */
    private fun getStrategyByType(type: StrategyType): ChatPromptStrategy {
        return strategies.find { it.getType() == type }
            ?: throw IllegalStateException("No strategy found for type: ${type.id}")
    }
    
    /**
     * 주어진 전략을 사용자 메시지로 실행합니다.
     * 
     * @param strategy 실행할 전략
     * @param userMessage 사용자 메시지
     * @return AI 응답
     */
    private fun executeStrategy(conversationId: String, strategy: ChatPromptStrategy, userMessage: String): ChatResponse {
        val prompt = strategy.createPrompt(userMessage)
        val chatOptions = strategy.getToolCallingChatOptions()
        return chatAssistant.call(conversationId, prompt, chatOptions)
    }

    /**
     * 최적화된 다단계 실행 계획을 병렬로 실행합니다.
     * 
     * @param chat 채팅 객체
     * @param executionPlan 실행 계획
     * @param originalUserMessage 원본 사용자 메시지
     * @return 최종 응답
     */
    private fun executeMultiStepPlanOptimized(chat: Chat, executionPlan: ReActResponse, originalUserMessage: String): String = runBlocking {
        val stepResults = ConcurrentHashMap<Int, String>()
        val completedSteps = mutableSetOf<Int>()
        
        logger.info("Starting optimized multi-step execution with ${executionPlan.actions.size} steps")
        
        // 단계별 의존성 그래프 생성
        val dependencyGraph = executionPlan.actions.associateBy { it.step }
        val readySteps = mutableSetOf<ActionStep>()
        
        // 의존성이 없는 첫 번째 단계들 찾기
        executionPlan.actions.filter { it.dependencies.isEmpty() }.forEach { readySteps.add(it) }
        
        while (readySteps.isNotEmpty() || completedSteps.size < executionPlan.actions.size) {
            // 준비된 단계들을 병렬로 실행
            val currentBatch = readySteps.toList()
            readySteps.clear()
            
            if (currentBatch.isNotEmpty()) {
                logger.info("Executing batch of ${currentBatch.size} parallel steps: ${currentBatch.map { it.step }}")
                
                val batchResults = currentBatch.map { step ->
                    async(Dispatchers.IO) {
                        try {
                            logger.info("Executing Step ${step.step}: ${step.strategy}")
                            
                            // 의존성 결과 수집
                            val dependencyResults = step.dependencies.mapNotNull { depStep ->
                                stepResults[depStep]?.let { "Step $depStep result: $it" }
                            }.joinToString("\n")
                            
                            // 컨텍스트 최적화: 필요한 정보만 포함
                            val optimizedContext = buildOptimizedContext(originalUserMessage, step, stepResults)
                            
                            // 단계별 실행
                            val stepResult = executeStepOptimized(chat, step, optimizedContext, dependencyResults)
                            
                            logger.info("Step ${step.step} completed successfully")
                            step.step to stepResult
                            
                        } catch (e: Exception) {
                            logger.error("Step ${step.step} failed: ${e.message}", e)
                            step.step to "Step ${step.step} failed: ${e.message}"
                        }
                    }
                }.awaitAll()
                
                // 결과 저장 및 완료된 단계 추가
                batchResults.forEach { (stepNum, result) ->
                    stepResults[stepNum] = result
                    completedSteps.add(stepNum)
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
                    logger.warn("Potential deadlock detected. Executing remaining steps sequentially.")
                    remainingSteps.forEach { step ->
                        try {
                            val dependencyResults = step.dependencies.mapNotNull { depStep ->
                                stepResults[depStep]?.let { "Step $depStep result: $it" }
                            }.joinToString("\n")
                            
                            val optimizedContext = buildOptimizedContext(originalUserMessage, step, stepResults)
                            val stepResult = executeStepOptimized(chat, step, optimizedContext, dependencyResults)
                            
                            stepResults[step.step] = stepResult
                            completedSteps.add(step.step)
                        } catch (e: Exception) {
                            logger.error("Step ${step.step} failed: ${e.message}", e)
                            stepResults[step.step] = "Step ${step.step} failed: ${e.message}"
                            completedSteps.add(step.step)
                        }
                    }
                }
                break
            }
        }
        
        // 최종 결과 반환
        val responseGenerationStep = executionPlan.actions.find { 
            StrategyType.findById(it.strategy)?.executionType == StrategyExecutionType.RESPONSE_GENERATION 
        }
        
        return@runBlocking if (responseGenerationStep != null) {
            stepResults[responseGenerationStep.step] ?: "No response generation result available"
        } else {
            // 응답 생성 전략이 없는 경우 마지막 단계 결과 반환
            val lastStep = executionPlan.actions.maxByOrNull { it.step }
            stepResults[lastStep?.step ?: 1] ?: "No results available"
        }
    }
    
    /**
     * 최적화된 컨텍스트를 생성합니다 (필요한 정보만 포함)
     */
    private fun buildOptimizedContext(originalUserMessage: String, currentStep: ActionStep, stepResults: ConcurrentHashMap<Int, String>): String {
        val relevantResults = currentStep.dependencies.mapNotNull { depStep ->
            stepResults[depStep]?.let { "Step $depStep result: $it" }
        }
        
        return if (relevantResults.isNotEmpty()) {
            "Original query: $originalUserMessage\n\nRelevant previous results:\n${relevantResults.joinToString("\n")}"
        } else {
            "Original query: $originalUserMessage"
        }
    }
    
    /**
     * 최적화된 개별 단계 실행 (컨텍스트 크기 최소화)
     */
    private fun executeStepOptimized(chat: Chat, step: ActionStep, optimizedContext: String, dependencyResults: String): String {
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
     * 전략 타입별로 체계적인 실행 순서를 관리합니다.
     */
    private fun optimizeExecutionPlan(executionPlan: ReActResponse): ReActResponse {
        val optimizedActions = executionPlan.actions.sortedWith(compareBy<ActionStep> { 
            when (StrategyType.findById(it.strategy)?.executionType) {
                StrategyExecutionType.DATA_COLLECTION -> 1
                StrategyExecutionType.DATA_PROCESSING -> 2
                StrategyExecutionType.DECISION_MAKING -> 3
                StrategyExecutionType.RESPONSE_GENERATION -> 4
                null -> 5
            }
        }.thenBy { it.step })
        
        // 단계 번호 재정렬
        val reorderedActions = optimizedActions.mapIndexed { index, action ->
            action.copy(step = index + 1)
        }
        
        logger.info("Optimized execution plan by strategy types:")
        reorderedActions.forEach { action ->
            val strategyType = StrategyType.findById(action.strategy)
            logger.info("  Step ${action.step}: ${action.strategy} (${strategyType?.executionType})")
        }
        
        return executionPlan.copy(actions = reorderedActions)
    }
    
    /**
     * 전략 타입별 병렬 실행 가능성을 판단합니다.
     */
    private fun canExecuteInParallel(action1: ActionStep, action2: ActionStep): Boolean {
        val type1 = StrategyType.findById(action1.strategy)?.executionType
        val type2 = StrategyType.findById(action2.strategy)?.executionType
        
        // 같은 타입이고 의존성이 없으면 병렬 실행 가능
        return type1 == type2 && 
               action1.dependencies.none { it == action2.step } &&
               action2.dependencies.none { it == action1.step }
    }
    
    /**
     * 유스케이스별 최적화된 캐시 키를 생성합니다.
     */
    private fun generateAdvancedCacheKey(userMessage: String): String {
        return when {
            // 유스케이스 1: 사례 기반 지역 범위 정의
            userMessage.contains("지역") && (userMessage.contains("범위") || userMessage.contains("정의")) -> 
                "area_scope_definition"
            
            // 유스케이스 2: 프로젝트 요구사항 기반 지역 분석 및 추천
            userMessage.contains("분석") && userMessage.contains("추천") -> 
                "location_analysis_recommendation"
            
            // 유스케이스 3: 특정 지역의 건물 추천
            userMessage.contains("건물") && userMessage.contains("추천") -> 
                "building_recommendation"
            
            // 유스케이스 4: 브랜드 특성 기반 팝업 사례 분석
            userMessage.contains("팝업") && (userMessage.contains("사례") || userMessage.contains("분석")) -> 
                "popup_case_study"
            
            // 유스케이스 5: 미래 예측 시나리오
            userMessage.contains("미래") || userMessage.contains("예측") || userMessage.contains("전망") -> 
                "future_prediction"
            
            // 기존 패턴들
            userMessage.contains("추천") && userMessage.contains("팝업") -> "popup_recommendation"
            userMessage.contains("비교") || userMessage.contains("어디가") -> "area_comparison"
            userMessage.contains("혼잡") || userMessage.contains("붐비") -> "congestion_query"
            userMessage.contains("건물") -> "building_query"
            userMessage.contains("지역") || userMessage.contains("역") -> "area_query"
            else -> "general_query"
        }
    }

    /**
     * 사용자 메시지를 스트림으로 처리하고 ReAct 실행 과정을 실시간으로 반환합니다.
     * 
     * @param chat 채팅 객체
     * @return ReAct 실행 상태 스트림
     */
    fun processUserMessageStream(chat: Chat): Flow<ReActStreamResponse> = flow {
        val chatId = chat.id.toString()
        val executionId = java.util.UUID.randomUUID().toString()
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()

        try {
            // 단순한 쿼리는 직접 처리
            if (isSimpleQuery(userMessage)) {
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
                        phase = ExecutionPhase.PLANNING,
                        currentStep = null,
                        totalSteps = 1,
                        progress = 0.2,
                        message = "간단한 쿼리로 인식, 직접 처리합니다"
                    )
                ))
                
                val result = processSimpleQuery(chat, userMessage)
                val finalMessage = result.getLatestAssistantMessage()?.content ?: "응답을 생성할 수 없습니다"
                
                emit(ReActStreamResponse(
                    status = ReActExecutionStatus(
                        chatId = chatId,
                        executionId = executionId,
                        phase = ExecutionPhase.COMPLETED,
                        currentStep = 1,
                        totalSteps = 1,
                        progress = 1.0,
                        message = "처리 완료"
                    ),
                    isComplete = true,
                    finalResult = finalMessage
                ))
                return@flow
            }

            // 실행 계획 생성 시작
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.PLANNING,
                    currentStep = null,
                    totalSteps = 0,
                    progress = 0.1,
                    message = "ReAct 실행 계획을 생성하고 있습니다..."
                )
            ))

            // 캐시된 실행 계획 확인 또는 새로 생성
            val cacheKey = generateAdvancedCacheKey(userMessage)
            val executionPlan = executionPlanCache[cacheKey] ?: run {
                val plan = createExecutionPlan(chat)
                val optimizedPlan = optimizeExecutionPlan(plan)
                executionPlanCache[cacheKey] = optimizedPlan
                optimizedPlan
            }

            // 실행 계획 완료
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.PLANNING,
                    currentStep = null,
                    totalSteps = executionPlan.actions.size,
                    progress = 0.2,
                    message = "실행 계획이 생성되었습니다 (총 ${executionPlan.actions.size}단계)"
                )
            ))

            // 다단계 실행 스트림
            val finalResult = executeMultiStepPlanStream(chat, executionPlan, userMessage, chatId, executionId)
                .collect { streamResponse ->
                    emit(streamResponse)
                }

        } catch (e: Exception) {
            logger.error("스트림 처리 중 오류 발생", e)
            emit(ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.FAILED,
                    currentStep = null,
                    totalSteps = 0,
                    progress = 0.0,
                    message = "처리 중 오류가 발생했습니다",
                    error = e.message
                ),
                isComplete = true
            ))
        }
    }

    /**
     * 다단계 실행 계획을 스트림으로 실행합니다.
     */
    private fun executeMultiStepPlanStream(
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
        val dependencyGraph = executionPlan.actions.associateBy { it.step }
        val readySteps = mutableSetOf<ActionStep>()
        
        // 의존성이 없는 첫 번째 단계들 찾기
        executionPlan.actions.filter { it.dependencies.isEmpty() }.forEach { readySteps.add(it) }
        
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
                val batchResults = runBlocking {
                    currentBatch.map { step ->
                        async(Dispatchers.IO) {
                            try {
                                logger.info("단계 ${step.step} 실행: ${step.strategy}")
                                
                                // 의존성 결과 수집
                                val dependencyResults = step.dependencies.mapNotNull { depStep ->
                                    stepResults[depStep]?.let { "Step $depStep result: $it" }
                                }.joinToString("\n")
                                
                                // 컨텍스트 최적화
                                val optimizedContext = buildOptimizedContext(originalUserMessage, step, stepResults)
                                
                                // 단계별 실행
                                val stepResult = executeStepOptimized(chat, step, optimizedContext, dependencyResults)
                                
                                logger.info("단계 ${step.step} 완료")
                                step.step to stepResult
                                
                            } catch (e: Exception) {
                                logger.error("단계 ${step.step} 실패: ${e.message}", e)
                                step.step to "단계 ${step.step} 실패: ${e.message}"
                            }
                        }
                    }.awaitAll()
                }
                
                // 결과 저장 및 완료 알림
                batchResults.forEach { (stepNum, result) ->
                    stepResults[stepNum] = result
                    completedSteps.add(stepNum)
                    
                    val step = executionPlan.actions.find { it.step == stepNum }
                    val isError = result.contains("실패")
                    
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
                            
                            val optimizedContext = buildOptimizedContext(originalUserMessage, step, stepResults)
                            val stepResult = executeStepOptimized(chat, step, optimizedContext, dependencyResults)
                            
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
        val responseGenerationStep = executionPlan.actions.find { 
            StrategyType.findById(it.strategy)?.executionType == StrategyExecutionType.RESPONSE_GENERATION 
        }
        
        val finalResult = if (responseGenerationStep != null) {
            stepResults[responseGenerationStep.step] ?: "응답 생성 결과를 찾을 수 없습니다"
        } else {
            val lastStep = executionPlan.actions.maxByOrNull { it.step }
            stepResults[lastStep?.step ?: 1] ?: "결과를 생성할 수 없습니다"
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
            finalResult = finalResult
        ))
    }
} 