package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatMessage
import com.wheretopop.shared.enums.ChatMessageRole
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

@Component
class DefaultChatPromptBuilder : ChatPromptBuilder {

    override suspend fun buildPrompt(chat: Chat, userMessage: ChatMessage): Prompt {
        val messages = mutableListOf<Message>()
        
        // 시스템 메시지 추가
        messages.add(SystemMessage("당신은 팝업 스토어에 관한 전문가 AI 어시스턴트입니다. " +
                "사용자의 프로젝트와 관련된 모든 질문에 친절하고 정확하게 답변해주세요."))
        
        // 기존 메시지 추가
        chat.messages.forEach { message ->
            when (message.role) {
                ChatMessageRole.USER -> messages.add(UserMessage(message.content))
                ChatMessageRole.ASSISTANT -> messages.add(AssistantMessage(message.content))
                ChatMessageRole.SYSTEM -> messages.add(SystemMessage(message.content))
            }
        }
        
        // 새 사용자 메시지 추가
        messages.add(UserMessage(userMessage.content))
        
        return Prompt(messages)
    }
}
