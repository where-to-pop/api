package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatScenario
import com.wheretopop.infrastructure.chat.ChatAssistant
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.stereotype.Component

/**
 * 사용자 메시지에 따라 적절한 전략을 선택하고 실행하는 관리자 클래스
 * 리팩터링된 버전: 각 책임별로 분리된 클래스들을 조합하는 오케스트레이터 역할
 */
@Component
class ChatPromptStrategyManager(
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>,
    private val reActExecutionPlanner: ReActExecutionPlanner,
    private val reActStreamProcessor: ReActStreamProcessor,
    private val performanceMonitor: PerformanceMonitor,
    private val tokenUsageTracker: TokenUsageTracker
): ChatScenario {
    private val logger = KotlinLogging.logger {}
    
    companion object {
        /**
         * 제목 생성에 사용할 최근 메시지 개수
         */
        private const val TITLE_CONTEXT_MESSAGE_COUNT = 3
    }
    
    /**
     * 사용자 메시지를 기반으로 채팅 제목을 생성합니다.
     */
    override fun generateTitle(chat: Chat): String {
        val latestUserMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        
        // 최근 메시지들을 컨텍스트로 활용
        val recentContext = chat.getRecentMessagesAsContext(TITLE_CONTEXT_MESSAGE_COUNT)
        val contextualMessage = if (recentContext.isNotBlank()) {
            """
            Recent conversation:
            $recentContext
            
            Generate a title based on this conversation context.
            """.trimIndent()
        } else {
            latestUserMessage
        }
        
        logger.info("Generating chat title with context for message: $latestUserMessage")
        
        return performanceMonitor.measureTimeSync("Title generation") {
            val titleStrategy = getStrategyByType(StrategyType.TITLE_GENERATION)
            val response = executeStrategyWithTracking(chat.id.toString(), titleStrategy, contextualMessage, "제목 생성")
            
            // XML 태그에서 제목만 추출
            val fullResponse = response.result.output.text?.trim()
                ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
            logger.info("Full response for title generation: $fullResponse")
            
            // <title>제목</title> 형식에서 제목만 추출
            val titleRegex = "<title>(.*?)</title>".toRegex()
            val matchResult = titleRegex.find(fullResponse)
            
            val extractedTitle = matchResult?.groupValues?.get(1)?.trim()
                ?: throw ErrorCode.CHAT_TITLE_EXTRACTION_FAILED.toException("No title found in response: $fullResponse")
            
            // 최대 길이 제한
            if (extractedTitle.length > 50) extractedTitle.take(47) + "…" else extractedTitle
        }
    }
    


    /**
     * 토큰 사용량 추적과 함께 전략을 실행합니다.
     */
    private fun executeStrategyWithTracking(
        conversationId: String, 
        strategy: ChatPromptStrategy, 
        userMessage: String,
        context: String
    ): ChatResponse {
        logger.info { "Selected Strategy: ${strategy.getType()}" }
        val response = chatAssistant.call(conversationId, strategy.createPrompt(userMessage), strategy.getToolCallingChatOptions())
        
        // 토큰 사용량 추적
        tokenUsageTracker.trackAndLogTokenUsage(response, context)
        
        return response
    }
    
    /**
     * 사용자 메시지를 스트림으로 처리하고 ReAct 실행 과정을 실시간으로 반환합니다.
     */
    override fun processUserMessageStream(chat: Chat): Flow<ReActStreamResponse> = flow {
        val chatId = chat.id.toString()
        val executionId = java.util.UUID.randomUUID().toString()
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()

        try {
            // 실행 계획 생성 시작
            emit(createPlanningStreamResponse(chatId, executionId, StrategyType.buildPhaseMessage(ExecutionPhase.PLANNING), 0.1))

            // 실행 계획 생성 (스트림 형태로 진행 상황 emit)
            var executionPlan: ReActResponse? = null
            reActExecutionPlanner.createExecutionPlan(chat, chatId, executionId)
                .collect { result ->
                    when (result) {
                        is ExecutionPlanningResult.Progress -> {
                            emit(result.streamResponse)
                        }
                        is ExecutionPlanningResult.Complete -> {
                            executionPlan = result.plan
                        }
                    }
                }

            // 실행 계획이 완료되면 실제 실행 시작
            executionPlan?.let { plan ->
                reActStreamProcessor.executeMultiStepPlanStream(chat, plan, userMessage, chatId, executionId)
                    .collect { streamResponse ->
                        emit(streamResponse)
                    }
            } ?: throw IllegalStateException("실행 계획 생성 실패")

        } catch (e: Exception) {
            logger.error("스트림 처리 중 오류 발생", e)
            emit(createErrorStreamResponse(chatId, executionId, e.message))
        }
    }

    /**
     * 주어진 전략 타입의 전략을 찾아 반환합니다.
     */
    private fun getStrategyByType(type: StrategyType): ChatPromptStrategy {
        return strategies.find { it.getType() == type }
            ?: throw IllegalStateException("No strategy found for type: ${type.id}")
    }


    private fun createPlanningStreamResponse(
        chatId: String, 
        executionId: String, 
        message: String, 
        progress: Double, 
        totalSteps: Int = 0
    ) = ReActStreamResponse(
        status = ReActExecutionStatus(
            chatId = chatId,
            executionId = executionId,
            phase = ExecutionPhase.PLANNING,
            currentStep = null,
            totalSteps = totalSteps,
            progress = progress,
            message = message
        )
    )
    
    private fun createErrorStreamResponse(chatId: String, executionId: String, errorMessage: String?) =
        ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chatId,
                executionId = executionId,
                phase = ExecutionPhase.FAILED,
                currentStep = null,
                totalSteps = 0,
                progress = 0.0,
                message = StrategyType.buildPhaseMessage(ExecutionPhase.FAILED),
                error = errorMessage
            ),
            isComplete = true
        )
} 