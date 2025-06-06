package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatMessage
import com.wheretopop.domain.chat.ChatScenario
import com.wheretopop.infrastructure.chat.ChatAssistant
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.shared.enums.ChatMessageRole
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * 사용자 메시지에 따라 적절한 전략을 선택하고 실행하는 관리자 클래스
 * 리팩터링된 버전: 각 책임별로 분리된 클래스들을 조합하는 오케스트레이터 역할
 */
@Component
class ChatPromptStrategyManager(
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>,
    private val reActExecutionPlanner: ReActExecutionPlanner,
    private val executionCacheManager: ExecutionCacheManager,
    private val multiStepExecutor: MultiStepExecutor,
    private val reActStreamProcessor: ReActStreamProcessor,
    private val performanceMonitor: PerformanceMonitor,
    private val tokenUsageTracker: TokenUsageTracker
): ChatScenario {
    private val logger = KotlinLogging.logger {}
    
    /**
     * 사용자 메시지를 기반으로 채팅 제목을 생성합니다.
     */
    override fun generateTitle(chat: Chat): String {
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        
        logger.info("Generating chat title for message: $userMessage")
        
        return performanceMonitor.measureTimeSync("Title generation") {
            val titleStrategy = getStrategyByType(StrategyType.TITLE_GENERATION)
            val response = executeStrategyWithTracking(chat.id.toString(), titleStrategy, userMessage, "제목 생성")
            
            // 응답에서 제목만 추출
            response.result.output.text?.trim()
                ?.let { if (it.length > 50) it.take(47) + "…" else it }
                ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        }
    }
    
    /**
     * 사용자 메시지를 처리하고 적절한 응답을 생성합니다.
     * ReAct 다단계 실행 계획을 순차적으로 실행합니다.
     */
    override fun processUserMessage(chat: Chat): Chat = performanceMonitor.measureTimeSync("Total message processing") {
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()


        // 캐시된 실행 계획 확인 또는 새로 생성  
        val finalResponse = performanceMonitor.measureTimeSync("Multi-step execution with caching") {
            // suspend 함수를 일반 함수에서 호출하기 위해 runBlocking 사용
            kotlinx.coroutines.runBlocking {
                executeWithCaching(chat, userMessage)
            }
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
     * 캐싱을 사용한 실행 계획 처리
     */
    private suspend fun executeWithCaching(chat: Chat, userMessage: String): String {
        val cacheKey = executionCacheManager.generateCacheKey(userMessage)
        val executionPlan = executionCacheManager.getExecutionPlan(cacheKey) ?: run {
            val plan = performanceMonitor.measureTimeSync("Execution plan creation") {
                reActExecutionPlanner.createExecutionPlan(chat) 
            }
            executionCacheManager.putExecutionPlan(cacheKey, plan)
            plan
        }
        
        // 최적화된 다단계 실행
        return multiStepExecutor.executeMultiStepPlan(chat, executionPlan, userMessage)
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
        val response = chatAssistant.call(conversationId, strategy.createPrompt(userMessage), strategy.getToolCallingChatOptions())
        
        // 토큰 사용량 추적
        tokenUsageTracker.trackAndLogTokenUsage(response, context)
        
        return response
    }
    
    /**
     * 사용자 메시지를 스트림으로 처리하고 ReAct 실행 과정을 실시간으로 반환합니다. (기존 호환성)
     */
    fun processUserMessageStream(chat: Chat): Flow<ReActStreamResponse> = flow {
        val chatId = chat.id.toString()
        val executionId = java.util.UUID.randomUUID().toString()
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()

        try {
            // 실행 계획 생성 시작
            emit(createPlanningStreamResponse(chatId, executionId, StrategyType.buildPhaseMessage(ExecutionPhase.PLANNING), 0.1))

            // 캐시된 실행 계획 확인 또는 새로 생성
            val cacheKey = executionCacheManager.generateCacheKey(userMessage)
            val executionPlan = executionCacheManager.getExecutionPlan(cacheKey) ?: run {
                val plan = reActExecutionPlanner.createExecutionPlan(chat)
                executionCacheManager.putExecutionPlan(cacheKey, plan)
                plan
            }

            reActStreamProcessor.executeMultiStepPlanStream(chat, executionPlan, userMessage, chatId, executionId)
                .collect { streamResponse ->
                    emit(streamResponse)
                }

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