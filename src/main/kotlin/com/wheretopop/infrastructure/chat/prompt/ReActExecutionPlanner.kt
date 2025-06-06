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
import mu.KotlinLogging
import org.springframework.stereotype.Component

/**
 * 요구사항 복잡도 레벨
 */
enum class ComplexityLevel {
    SIMPLE,     // 단순 - 일반 응답만으로 충분
    MODERATE,   // 보통 - 1-2개 데이터 소스 필요
    COMPLEX     // 복잡 - 다중 데이터 소스 및 분석 필요
}

/**
 * 요구사항 분석 결과
 */
data class RequirementAnalysis(
    val userIntent: String,           // 사용자 의도
    val processedQuery: String,       // 가공된 쿼리
    val complexityLevel: ComplexityLevel, // 복잡도
    val requiredDataSources: List<String>, // 필요한 데이터 소스
    val contextSummary: String,       // 컨텍스트 요약
    val reasoning: String             // 분석 근거
)

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
     * ReAct 실행 계획을 생성합니다.
     */
    fun createExecutionPlan(chat: Chat): ReActResponse {
        // 1단계: 요구사항 분석 및 복잡도 평가
        val requirementAnalysis = analyzeRequirement(chat)
        logger.info("요구사항 분석 완료: 복잡도=${requirementAnalysis.complexityLevel}")
        
        // 2단계: 복잡도에 따른 적응적 계획 수립
        return when (requirementAnalysis.complexityLevel) {
            ComplexityLevel.SIMPLE -> createSimplePlan(requirementAnalysis)
            ComplexityLevel.MODERATE -> createModeratePlan(chat, requirementAnalysis)
            ComplexityLevel.COMPLEX -> createComplexPlan(chat, requirementAnalysis)
        }
    }
    
    /**
     * 요구사항을 분석하고 복잡도를 평가합니다.
     */
    private fun analyzeRequirement(chat: Chat): RequirementAnalysis {
        val requirementAnalyzer = getStrategyByType(StrategyType.REQUIREMENT_ANALYSIS)
        
        // 최근 메시지들을 컨텍스트로 구성
        val recentContext = chat.getRecentMessagesAsContext(CONTEXT_MESSAGE_COUNT)
        val latestUserMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        
        val contextualMessage = if (recentContext.isNotBlank()) {
            """
            Recent conversation context:
            $recentContext
            
            Current user message to analyze:
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
                requiredDataSources = listOf("general_knowledge"),
                contextSummary = "요구사항 분석 실패로 단순 처리",
                reasoning = "분석 실패로 인한 폴백"
            )
        }
    }
    
    /**
     * 단순한 요구사항에 대한 계획 생성
     */
    private fun createSimplePlan(analysis: RequirementAnalysis): ReActResponse {
        logger.info("단순 계획 생성: 일반 응답만 사용")
        return ReActResponse(
            thought = "단순한 질문으로 분석됨. 일반적인 지식 기반 응답으로 충분함.",
            actions = listOf(
                ActionStep(
                    step = 1,
                    strategy = StrategyType.GENERAL_RESPONSE.id,
                    purpose = analysis.userIntent,
                    reasoning = "단순한 요구사항으로 추가 데이터 수집 불필요",
                    recommended_tools = emptyList(),
                    tool_sequence = "Direct general response",
                    expected_output = "사용자 질문에 대한 포괄적인 답변",
                    dependencies = emptyList()
                )
            ),
            observation = "단순한 요구사항으로 단일 전략 적용"
        )
    }
    
    /**
     * 보통 복잡도 요구사항에 대한 ReAct 계획 생성
     */
    private fun createModeratePlan(chat: Chat, analysis: RequirementAnalysis): ReActResponse {
        logger.info("보통 복잡도 계획 생성: ReAct 플래닝 사용")
        return createReActPlan(chat, analysis, "MODERATE")
    }
    
    /**
     * 복잡한 요구사항에 대한 ReAct 계획 생성
     */
    private fun createComplexPlan(chat: Chat, analysis: RequirementAnalysis): ReActResponse {
        logger.info("복잡한 계획 생성: ReAct 플래닝 사용")
        return createReActPlan(chat, analysis, "COMPLEX")
    }
    
    /**
     * ReAct 플래닝을 사용한 계획 생성 (MODERATE/COMPLEX 공통)
     */
    private fun createReActPlan(chat: Chat, analysis: RequirementAnalysis, complexityLevel: String): ReActResponse {
        val reActPlanner = getStrategyByType(StrategyType.REACT_PLANNER)
        val originalUserMessage = chat.getLatestUserMessage()?.content ?: ""
        
        val contextualMessage = """
            Original User Message: "$originalUserMessage"
            
            Complexity Level: $complexityLevel
            Processed user requirement: ${analysis.processedQuery}
            User intent: ${analysis.userIntent}
            Required data sources: ${analysis.requiredDataSources.joinToString(", ")}
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
            reActResponse
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
            logger.info("    - Tools: ${step.recommended_tools}")
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