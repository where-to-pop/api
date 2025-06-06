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
import kotlinx.coroutines.delay
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
    
    /**
     * ReAct 실행 계획을 생성합니다.
     */
    fun createExecutionPlan(chat: Chat): ReActResponse {
        val reActPlanner = getStrategyByType(StrategyType.REACT_PLANNER)
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        
        val response = executeStrategy(chat.id.toString(), reActPlanner, userMessage)
        val responseText = response.result.output.text?.trim() 
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        
        logger.info("ReAct execution planning response: $responseText")
        
        return try {
            val reActResponse = parseReActResponse(responseText)
            logExecutionPlan(reActResponse)
            reActResponse
        } catch (e: Exception) {
            logger.error("Failed to parse ReAct response: $responseText", e)
            val fallbackStrategyId = extractStrategyIdFallback(responseText)
            createFallbackExecutionPlan(fallbackStrategyId)
        }
    }
    
    /**
     * 실시간 사고 과정과 함께 ReAct 실행 계획을 생성합니다.
     */
    fun createExecutionPlanStream(chat: Chat, chatId: String, executionId: String): Flow<ChatStreamResponse> = flow {
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        
        try {
            // 사고 과정 시작
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.THINKING,
                thinkingMessage = "사용자의 요청을 분석하고 있습니다..."
            ))
            delay(500)
            
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.THINKING,
                thinkingMessage = "어떤 정보들이 필요할지 생각해보고 있습니다..."
            ))
            delay(700)
            
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.THINKING,
                thinkingMessage = "최적의 전략 조합을 계획하고 있습니다..."
            ))
            delay(600)
            
            // 실제 계획 생성
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.STEP_PLANNING,
                statusMessage = "실행 계획을 생성하고 있습니다..."
            ))
            
            val reActPlanner = getStrategyByType(StrategyType.REACT_PLANNER)
            val response = executeStrategy(chat.id.toString(), reActPlanner, userMessage)
            val responseText = response.result.output.text?.trim() 
                ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
            
            val executionPlan = try {
                val plan = parseReActResponse(responseText)
                logExecutionPlan(plan)
                plan
            } catch (e: Exception) {
                logger.error("Failed to parse ReAct response: $responseText", e)
                val fallbackStrategyId = extractStrategyIdFallback(responseText)
                createFallbackExecutionPlan(fallbackStrategyId)
            }
            
            // 계획 완료
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.STATUS_UPDATE,
                totalSteps = executionPlan.actions.size,
                progress = 0.2,
                statusMessage = "실행 계획이 완료되었습니다 (총 ${executionPlan.actions.size}단계)"
            ))
            
            // 계획 결과를 포함한 응답 (다른 컴포넌트에서 사용하기 위해)
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.COMPLETED,
                statusMessage = "계획 생성 완료"
            ))
            
        } catch (e: Exception) {
            logger.error("Planning stream failed", e)
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.ERROR,
                errorMessage = "계획 생성 중 오류가 발생했습니다: ${e.message}",
                errorCode = "PLANNING_ERROR"
            ))
        }
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