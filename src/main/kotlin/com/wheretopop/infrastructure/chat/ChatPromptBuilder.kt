package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatMessage
import org.springframework.ai.chat.prompt.Prompt

interface ChatPromptBuilder {
    /**
     * 채팅 객체로부터 프롬프트를 생성합니다.
     * 채팅 객체에 이미 모든 메시지가 포함되어 있다고 가정합니다.
     */
    suspend fun buildPrompt(chat: Chat, userMessage: ChatMessage): Prompt
}
