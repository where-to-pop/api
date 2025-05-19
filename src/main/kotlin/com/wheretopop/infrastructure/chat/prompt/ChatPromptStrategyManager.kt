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



/**
 * 사용자 메시지에 따라 적절한 전략을 선택하고 실행하는 관리자 클래스
 */
@Component
class ChatPromptStrategyManager(
    private val chatAssistant: ChatAssistant,
    private val strategies: List<ChatPromptStrategy>
): ChatScenario {
    private val logger = KotlinLogging.logger {}
    
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
        return response.result.output.text?.trim() ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException();
    }
    
    /**
     * 사용자 메시지를 처리하고 적절한 응답을 생성합니다.
     * 내부적으로 적합한 전략을 선택하여 실행합니다.
     * 
     * @param userMessage 사용자 메시지
     * @return AI 응답
     */
    override fun processUserMessage(chat: Chat): Chat {
        // 전략 선택기를 사용하여 적합한 전략 ID 결정
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()

        val selectedStrategyId = selectStrategyId(chat)
        val strategyType = StrategyType.findById(selectedStrategyId) ?: StrategyType.AREA_QUERY
        val selectedStrategy = getStrategyByType(strategyType)
        val response =  executeStrategy(chat.id.toString(),selectedStrategy, userMessage).result?.output?.text?.trim()
            ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()

        logger.info("Selected strategy ID: $selectedStrategyId")
        logger.info("AI response: $response")
        val messageAddedChat = chat.addMessage(ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.ASSISTANT,
            content = response,
            finishReason = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        ))
        return messageAddedChat
    }
    
    /**
     * 메시지에 적합한 전략 ID를 선택합니다.
     * 내부적으로 StrategySelectorStrategy를 사용합니다.
     * 
     * @param userMessage 사용자 메시지
     * @return 선택된 전략 ID
     */
    private fun selectStrategyId(chat: Chat): String {
        val selectorStrategy = getStrategyByType(StrategyType.STRATEGY_SELECTOR)
        val userMessage = chat.getLatestUserMessage()?.content
            ?: throw ErrorCode.COMMON_SYSTEM_ERROR.toException()
        val response = executeStrategy(chat.id.toString() ,selectorStrategy, userMessage)
        
        // 응답에서 전략 ID만 추출
        val strategyId = response.result.output.text?.trim() ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
        
        // 유효한 전략 ID인지 확인
        return if (StrategyType.entries.any { it.id == strategyId }) {
            strategyId
        } else {
            // 기본값으로 지역 쿼리 전략 사용
            logger.warn("Invalid strategy ID received: $strategyId, using default strategy")
            StrategyType.AREA_QUERY.id
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
} 