package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.shared.enums.ChatMessageRole
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

/**
 * 일반 대화를 위한 프롬프트 전략
 */
@Component
class ConversationPromptStrategy : ChatPromptStrategy {
    private val requirements = setOf(
        PromptContextRequirement.CHAT,
        PromptContextRequirement.USER_MESSAGE
    )

    override fun getRequirements(): Set<PromptContextRequirement> = requirements

    override fun canHandle(context: PromptContext): Boolean {
        return context.chat.messages.isNotEmpty() && 
               context.message?.role == ChatMessageRole.USER
    }

    override suspend fun buildPrompt(context: PromptContext): Prompt {
        require(canHandle(context)) { "Context requirements not met for ConversationPromptStrategy" }

        val messagePrefix = when (context.message?.role) {
            ChatMessageRole.SYSTEM -> "시스템 지시사항"
            ChatMessageRole.USER -> "사용자 메시지"
            ChatMessageRole.ASSISTANT -> "어시스턴트 응답"
            else -> "메시지"
        }

        return Prompt("""
            이전 대화 기록을 참고하여 자연스럽게 응답해주세요.
            
            대화 기록:
            ${context.chat.messages.joinToString("\n") { "${it.role}: ${it.content}" }}
            
            $messagePrefix:
            ${context.message?.role}: ${context.message?.content}
        """.trimIndent())
    }
} 