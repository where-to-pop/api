package com.wheretopop.infrastructure.chat.prompt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wheretopop.domain.chat.Chat
import com.wheretopop.infrastructure.chat.ChatAssistant
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.springframework.stereotype.Component


/**
 * ReAct 실행 계획 생성 및 파싱을 담당하는 클래스
 */
@Component
class ReActExecutionPlanner(
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>,
    private val tokenUsageTracker: TokenUsageTracker
) {
    private val logger = KotlinLogging.logger {}
    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    
    companion object {
        /**
         * ReAct 실행 계획 생성에 사용할 최근 메시지 개수
         * ChatConfig의 maxMessages(50)와 일치하도록 설정
         */
        private const val CONTEXT_MESSAGE_COUNT = 10
    }
    
    /**
     * ReAct 실행 계획을 생성합니다 (스트림 형태).
     */
    fun createExecutionPlan(chat: Chat, chatId: String, executionId: String, context: String?): Flow<ExecutionPlanningResult> = flow {
        // 1단계: 요구사항 분석 및 복잡도 평가
        emit(ExecutionPlanningResult.Progress(
            ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.PLANNING,
                    currentStep = null,
                    totalSteps = 0,
                    progress = 0.15,
                    message = "요구사항을 분석하고 있어요"
                )
            )
        ))
        
        val requirementAnalysis = analyzeRequirement(chat, context)
        logger.info("요구사항 분석 완료: 복잡도=${requirementAnalysis.complexityLevel}")
        
        // 2단계: 복잡도에 따른 적응적 계획 수립
        val executionInput = when (requirementAnalysis.complexityLevel) {
            ComplexityLevel.SIMPLE -> {
                emit(ExecutionPlanningResult.Progress(
                    ReActStreamResponse(
                        status = ReActExecutionStatus(
                            chatId = chatId,
                            executionId = executionId,
                            phase = ExecutionPhase.PLANNING,
                            currentStep = null,
                            totalSteps = 1,
                            progress = 0.25,
                            message = "바로 답변을 준비할게요!"
                        )
                    )
                ))
                createSimplePlan(requirementAnalysis)
            }
            ComplexityLevel.MODERATE -> {
                emit(ExecutionPlanningResult.Progress(
                    ReActStreamResponse(
                        status = ReActExecutionStatus(
                            chatId = chatId,
                            executionId = executionId,
                            phase = ExecutionPhase.PLANNING,
                            currentStep = null,
                            totalSteps = 0,
                            progress = 0.2,
                            message = "상세한 실행 계획을 세우고 있어요"
                        )
                    )
                ))
                createModeratePlan(chat, requirementAnalysis)
            }
            ComplexityLevel.COMPLEX -> {
                emit(ExecutionPlanningResult.Progress(
                    ReActStreamResponse(
                        status = ReActExecutionStatus(
                            chatId = chatId,
                            executionId = executionId,
                            phase = ExecutionPhase.PLANNING,
                            currentStep = null,
                            totalSteps = 0,
                            progress = 0.2,
                            message = "복잡한 요구사항이에요. 다단계 분석 계획을 세우고 있어요"
                        )
                    )
                ))
                createComplexPlan(chat, requirementAnalysis)
            }
        }
        
        // 최종 계획 완료 알림
        emit(ExecutionPlanningResult.Progress(
            ReActStreamResponse(
                status = ReActExecutionStatus(
                    chatId = chatId,
                    executionId = executionId,
                    phase = ExecutionPhase.PLANNING,
                    currentStep = null,
                    totalSteps = executionInput.reActResponse.actions.size,
                    progress = 0.3,
                    message = "실행 계획을 완료했어요! 총 ${executionInput.reActResponse.actions.size}단계로 진행합니다."
                )
            )
        ))
        
        // 최종 결과 emit
        emit(ExecutionPlanningResult.Complete(executionInput))
    }
    
    /**
     * 요구사항을 분석하고 복잡도를 평가합니다.
     */
    private fun analyzeRequirement(chat: Chat, context: String?): RequirementAnalysis {
        val requirementAnalyzer = getStrategyByType(StrategyType.REQUIREMENT_ANALYSIS)
        
        // 최근 메시지들을 컨텍스트로 구성
        val recentContext = chat.getRecentMessagesAsContext(CONTEXT_MESSAGE_COUNT)
        val latestUserMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        
        val contextualMessage = if (recentContext.isNotBlank()) {
            """
            ## Context
            $context
            
            ## Recent Conversation History
            $recentContext
            
            ## Current user message to analyze:
            $latestUserMessage
            """.trimIndent()
        } else {
            latestUserMessage
        }
        
        val response = executeStrategy(chat.id.toString(), requirementAnalyzer, contextualMessage)
        val responseText = response.result.output.text?.trim()
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        
        return try {
            parseRequirementAnalysis(responseText)
        } catch (e: Exception) {
            logger.error("Failed to parse requirement analysis: $responseText", e)
            // 폴백: 단순한 요구사항으로 처리
            RequirementAnalysis(
                userIntent = latestUserMessage,
                processedQuery = latestUserMessage,
                complexityLevel = ComplexityLevel.SIMPLE,
                contextSummary = "요구사항 분석 실패로 단순 처리",
                reasoning = "분석 실패로 인한 폴백"
            )
        }
    }
    
    /**
     * 단순한 요구사항에 대한 계획 생성
     */
    private fun createSimplePlan(analysis: RequirementAnalysis): ReActExecutionInput {
        logger.info("단순 계획 생성: 일반 응답만 사용")
        return ReActExecutionInput(
            reActResponse = ReActResponse(
            thought = "단순한 질문으로 분석됨. 일반적인 지식 기반 응답으로 충분함.",
            actions = listOf(
                ActionStep(
                    step = 1,
                    strategy = StrategyType.GENERAL_RESPONSE.id,
                    purpose = analysis.userIntent,
                    reasoning = "단순한 요구사항으로 추가 데이터 수집 불필요",
                    expected_output = "사용자 질문에 대한 포괄적인 답변",
                    dependencies = emptyList()
                )
            ),
            observation = "단순한 요구사항으로 단일 전략 적용",
            ),
            requirementAnalysis = analysis
        )
    }
    
    /**
     * 보통 복잡도 요구사항에 대한 ReAct 계획 생성
     */
    private fun createModeratePlan(chat: Chat, analysis: RequirementAnalysis): ReActExecutionInput {
        logger.info("보통 복잡도 계획 생성: ReAct 플래닝 사용")
        return createReActPlan(chat, analysis, "MODERATE")
    }
    
    /**
     * 복잡한 요구사항에 대한 ReAct 계획 생성
     */
    private fun createComplexPlan(chat: Chat, analysis: RequirementAnalysis): ReActExecutionInput {
        logger.info("복잡한 계획 생성: ReAct 플래닝 사용")
        return createReActPlan(chat, analysis, "COMPLEX")
    }
    
    /**
     * ReAct 플래닝을 사용한 계획 생성 (MODERATE/COMPLEX 공통)
     */
    private fun createReActPlan(chat: Chat, analysis: RequirementAnalysis, complexityLevel: String): ReActExecutionInput {
        val reActPlanner = getStrategyByType(StrategyType.REACT_PLANNER)
        val originalUserMessage = chat.getLatestUserMessage()?.content ?: ""
        
        val contextualMessage = """
            Original User Message: "$originalUserMessage"
            
            Complexity Level: $complexityLevel
            Processed user requirement: ${analysis.processedQuery}
            User intent: ${analysis.userIntent}
            Context summary: ${analysis.contextSummary}
            Analysis reasoning: ${analysis.reasoning}
            
            Create an execution plan appropriate for this $complexityLevel complexity requirement.
            Make sure to address the original user message directly.
        """.trimIndent()
        
        val response = executeStrategy(chat.id.toString(), reActPlanner, contextualMessage)
        val responseText = response.result.output.text?.trim()
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        
        return try {
            val reActResponse = parseReActResponse(responseText)
            validateRAGPattern(reActResponse)
            logExecutionPlan(reActResponse)
            ReActExecutionInput(reActResponse, analysis)
        } catch (e: Exception) {
            logger.error("Failed to parse $complexityLevel ReAct response: $responseText", e)
            val fallbackStrategyId = extractStrategyIdFallback(responseText)
            createFallbackExecutionPlan(fallbackStrategyId)
        }
    }
    
    /**
     * 요구사항 분석 결과를 파싱합니다.
     */
    private fun parseRequirementAnalysis(responseText: String): RequirementAnalysis {
        val jsonPattern = Regex("```json\\s*([\\s\\S]*?)\\s*```")
        val jsonMatch = jsonPattern.find(responseText)
        val jsonText = jsonMatch?.groupValues?.get(1) ?: responseText
        return objectMapper.readValue<RequirementAnalysis>(jsonText)
    }
    
    /**
     * ReAct 응답을 파싱합니다.
     */
    private fun parseReActResponse(responseText: String): ReActResponse {
        val jsonPattern = Regex("```json\\s*([\\s\\S]*?)\\s*```")
        val jsonMatch = jsonPattern.find(responseText)
        val jsonText = jsonMatch?.groupValues?.get(1) ?: responseText
        return objectMapper.readValue<ReActResponse>(jsonText)
    }
    

    
    /**
     * 폴백 실행 계획을 생성합니다.
     */
    private fun createFallbackExecutionPlan(strategyId: String): ReActExecutionInput {
        return ReActExecutionInput(
            reActResponse = ReActResponse(
                thought = "Fallback execution plan due to parsing failure",
                actions = listOf(
                    ActionStep(
                        step = 1,
                        strategy = strategyId,
                        purpose = "Execute fallback strategy",
                        reasoning = "Using fallback strategy due to plan parsing failure",
                        expected_output = "Strategy-specific response",
                        dependencies = emptyList()
                    )
                ),
                observation = "Fallback plan created for single strategy execution",
            ),
            requirementAnalysis = RequirementAnalysis("fallback", "fallback", ComplexityLevel.SIMPLE, "null", "null")
        )
    }
    
    /**
     * JSON 파싱 실패 시 사용하는 폴백 전략 ID 추출
     */
    private fun extractStrategyIdFallback(responseText: String): String {
        val strategyIds = StrategyType.entries.map { it.id }
        
        for (strategyId in strategyIds) {
            if (responseText.contains(strategyId, ignoreCase = true)) {
                return strategyId
            }
        }
        
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
            
            else -> StrategyType.GENERAL_RESPONSE.id
        }
    }
    
    /**
     * RAG 패턴 검증: 마지막 단계가 Response Generation인지 확인
     */
    private fun validateRAGPattern(reActResponse: ReActResponse) {
        if (reActResponse.actions.isEmpty()) {
            throw IllegalStateException("Empty execution plan is not allowed")
        }
        
        val lastStep = reActResponse.actions.maxByOrNull { it.step }
            ?: throw IllegalStateException("No steps found in execution plan")
        
        val lastStepStrategy = StrategyType.findById(lastStep.strategy)
        
        if (lastStepStrategy?.executionType != com.wheretopop.infrastructure.chat.prompt.strategy.StrategyExecutionType.RESPONSE_GENERATION) {
            throw IllegalStateException(
                "❌ RAG Pattern validation failed: Last step must be RESPONSE_GENERATION strategy. " +
                "Found: ${lastStep.strategy} (${lastStepStrategy?.executionType}). " +
                "Please ensure the execution plan follows R+A (Retrieval+Augmentation) → G (Generation) pattern."
            )
        }
        
        logger.info("✅ RAG Pattern validation passed: Last step is ${lastStep.strategy} (RESPONSE_GENERATION)")
    }
    
    /**
     * 실행 계획을 로그로 출력합니다.
     */
    private fun logExecutionPlan(reActResponse: ReActResponse) {
        logger.info("ReAct Multi-Step Execution Plan:")
        logger.info("- Thought: ${reActResponse.thought}")
        logger.info("- Total Steps: ${reActResponse.actions.size}")
        
        reActResponse.actions.forEachIndexed { _, step ->
            logger.info("  Step ${step.step}:")
            logger.info("    - Strategy: ${step.strategy}")
            logger.info("    - Purpose: ${step.purpose}")
            logger.info("    - Reasoning: ${step.reasoning}")
            logger.info("    - Expected Output: ${step.expected_output}")
            logger.info("    - Dependencies: ${step.dependencies}")
        }
        
        logger.info("- Observation: ${reActResponse.observation}")
    }
    
    private fun getStrategyByType(type: StrategyType): ChatPromptStrategy {
        return strategies.find { it.getType() == type }
            ?: throw IllegalStateException("No strategy found for type: ${type.id}")
    }
    
    private fun executeStrategy(conversationId: String, strategy: ChatPromptStrategy, userMessage: String): org.springframework.ai.chat.model.ChatResponse {
        val response = chatAssistant.call(conversationId, strategy.createPrompt(userMessage), strategy.getToolCallingChatOptions())
        
        // 토큰 사용량 추적
        tokenUsageTracker.trackAndLogTokenUsage(response, "ReAct 계획 생성 - ${strategy.getType().id}")
        
        return response
    }
} 