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
    private val performanceMonitor: PerformanceMonitor
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
            val response = executeStrategy(chat.id.toString(), titleStrategy, userMessage)
            
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

        // 단순한 쿼리는 직접 처리 (다단계 실행 생략)
        if (isSimpleQuery(userMessage)) {
            logger.info("Processing simple query directly")
            return@measureTimeSync processSimpleQuery(chat, userMessage)
        }

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
     * 단순한 쿼리를 직접 처리합니다.
     */
    private fun processSimpleQuery(chat: Chat, userMessage: String): Chat {
        return performanceMonitor.measureTimeSync("Simple query processing") {
            val strategy = getStrategyByType(StrategyType.GENERAL_RESPONSE) // 폴백 전략 사용
            val response = executeStrategy(chat.id.toString(), strategy, userMessage)
            val responseText = response.result.output.text?.trim() 
                ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
            
            chat.addMessage(ChatMessage.create(
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
    }
    
    /**
     * 사용자 메시지를 스트림으로 처리하고 ReAct 실행 과정을 실시간으로 반환합니다. (새로운 모델)
     */
    fun processUserMessageStreamV2(chat: Chat): Flow<ChatStreamResponse> = flow {
        val chatId = chat.id.toString()
        val executionId = java.util.UUID.randomUUID().toString()
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()

        try {
            // 단순한 쿼리는 직접 처리
            if (isSimpleQuery(userMessage)) {
                emit(ChatStreamResponse(
                    chatId = chatId,
                    executionId = executionId,
                    type = StreamMessageType.THINKING,
                    thinkingMessage = "간단한 질문이네요! 바로 답변드릴게요."
                ))
                
                val result = processSimpleQuery(chat, userMessage)
                val finalMessage = result.getLatestAssistantMessage()?.content ?: "응답을 생성할 수 없습니다"
                
                // 응답을 글자별로 스트림
                finalMessage.chunked(1).forEachIndexed { index, chunk ->
                    kotlinx.coroutines.delay(25) // 타이핑 효과
                    emit(ChatStreamResponse(
                        chatId = chatId,
                        executionId = executionId,
                        type = StreamMessageType.RESPONSE_CHUNK,
                        responseChunk = chunk,
                        progress = index.toDouble() / finalMessage.length
                    ))
                }
                
                emit(ChatStreamResponse(
                    chatId = chatId,
                    executionId = executionId,
                    type = StreamMessageType.COMPLETED,
                    isComplete = true,
                    finalResponse = finalMessage,
                    progress = 1.0
                ))
                return@flow
            }

            // 실행 계획 생성 스트림 (사고 과정 포함)
            reActExecutionPlanner.createExecutionPlanStream(chat, chatId, executionId)
                .collect { planningResponse ->
                    emit(planningResponse)
                }

            // 캐시된 실행 계획 확인 또는 새로 생성
            val cacheKey = executionCacheManager.generateCacheKey(userMessage)
            val executionPlan = executionCacheManager.getExecutionPlan(cacheKey) ?: run {
                val plan = reActExecutionPlanner.createExecutionPlan(chat)
                executionCacheManager.putExecutionPlan(cacheKey, plan)
                plan
            }

            // 다단계 실행 스트림 (도구 실행 및 응답 생성 포함)
            reActStreamProcessor.executeMultiStepPlanStreamV2(chat, executionPlan, userMessage, chatId, executionId)
                .collect { streamResponse ->
                    emit(streamResponse)
                }

        } catch (e: Exception) {
            logger.error("스트림 처리 중 오류 발생", e)
            emit(ChatStreamResponse(
                chatId = chatId,
                executionId = executionId,
                type = StreamMessageType.ERROR,
                errorMessage = "처리 중 오류가 발생했습니다: ${e.message}",
                errorCode = "STREAM_ERROR"
            ))
        }
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
            // 단순한 쿼리는 직접 처리
            if (isSimpleQuery(userMessage)) {
                emit(createSimpleQueryStreamResponse(chatId, executionId, "간단한 쿼리로 인식, 직접 처리합니다", 0.2))
                
                val result = processSimpleQuery(chat, userMessage)
                val finalMessage = result.getLatestAssistantMessage()?.content ?: "응답을 생성할 수 없습니다"
                
                emit(createCompletedStreamResponse(chatId, executionId, finalMessage))
                return@flow
            }

            // 실행 계획 생성 시작
            emit(createPlanningStreamResponse(chatId, executionId, "ReAct 실행 계획을 생성하고 있습니다...", 0.1))

            // 캐시된 실행 계획 확인 또는 새로 생성
            val cacheKey = executionCacheManager.generateCacheKey(userMessage)
            val executionPlan = executionCacheManager.getExecutionPlan(cacheKey) ?: run {
                val plan = reActExecutionPlanner.createExecutionPlan(chat)
                executionCacheManager.putExecutionPlan(cacheKey, plan)
                plan
            }

            // 실행 계획 완료
            emit(createPlanningStreamResponse(
                chatId, executionId, 
                "실행 계획이 생성되었습니다 (총 ${executionPlan.actions.size}단계)", 
                0.2, 
                executionPlan.actions.size
            ))

            // 다단계 실행 스트림
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
     * 단순한 쿼리인지 판단합니다.
     */
    private fun isSimpleQuery(userMessage: String): Boolean {
        val simplePatterns = listOf(
             "감사", "고마워", "도움말", "help"
        )
        return simplePatterns.any { userMessage.contains(it, ignoreCase = true) } ||
               userMessage.length < 10
    }
    
    /**
     * 주어진 전략 타입의 전략을 찾아 반환합니다.
     */
    private fun getStrategyByType(type: StrategyType): ChatPromptStrategy {
        return strategies.find { it.getType() == type }
            ?: throw IllegalStateException("No strategy found for type: ${type.id}")
    }
    
    /**
     * 주어진 전략을 사용자 메시지로 실행합니다.
     */
    private fun executeStrategy(conversationId: String, strategy: ChatPromptStrategy, userMessage: String) =
        chatAssistant.call(conversationId, strategy.createPrompt(userMessage), strategy.getToolCallingChatOptions())
    
    // Stream Response 생성 헬퍼 메서드들
    private fun createSimpleQueryStreamResponse(chatId: String, executionId: String, message: String, progress: Double) =
        ReActStreamResponse(
            status = ReActExecutionStatus(
                chatId = chatId,
                executionId = executionId,
                phase = ExecutionPhase.PLANNING,
                currentStep = null,
                totalSteps = 1,
                progress = progress,
                message = message
            )
        )
    
    private fun createCompletedStreamResponse(chatId: String, executionId: String, finalMessage: String) =
        ReActStreamResponse(
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
        )
    
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
                message = "처리 중 오류가 발생했습니다",
                error = errorMessage
            ),
            isComplete = true
        )
} 