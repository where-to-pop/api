package com.wheretopop.infrastructure.chat.prompt

import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * 사용자의 첫 메시지를 기반으로 채팅 대화방의 제목을 생성하는 전략 구현체
 */
@Component
class ChatTitleGenerationStrategy : BaseChatPromptStrategy() {

    /**
     * 전략 타입을 반환합니다.
     */
    override fun getType(): StrategyType {
        return StrategyType.TITLE_GENERATION
    }

    /**
     * 첫 메시지 기반 채팅 제목 생성을 위한 기본 프롬프트 대신 
     * 제목 생성에 특화된 프롬프트를 사용합니다.
     */
    override fun getSystemPrompt(): String {
        return SystemPrompt.BASE_PROMPT
    }
    
    /**
     * 채팅 제목 생성에 특화된 추가 프롬프트를 반환합니다.
     */
    override fun getAdditionalSystemPrompt(): String {
        return SystemPrompt.CHAT_TITLE_PROMPT
    }

    /**
     * 제목 생성은 단순한 작업이므로 Tool Calling을 사용하지 않습니다.
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {
        return null
    }
} 