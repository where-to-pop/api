package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatMessage

/**
 * 프롬프트 생성에 필요한 컨텍스트를 담는 클래스
 */
data class PromptContext private constructor(
    val chat: Chat,
    val message: ChatMessage?,
    val systemMessage: String?,
    val additionalContext: Map<String, Any>
) {
    class Builder {
        private var chat: Chat? = null
        private var message: ChatMessage? = null
        private var systemMessage: String? = null
        private val additionalContext = mutableMapOf<String, Any>()

        fun chat(chat: Chat) = apply { this.chat = chat }
        fun message(message: ChatMessage?) = apply { this.message = message }
        fun systemMessage(message: String?) = apply { this.systemMessage = message }
        fun addContext(key: String, value: Any) = apply { this.additionalContext[key] = value }

        fun build(): PromptContext {
            requireNotNull(chat) { "Chat is required" }
            return PromptContext(
                chat = chat!!,
                message = message,
                systemMessage = systemMessage,
                additionalContext = additionalContext.toMap()
            )
        }
    }

    companion object {
        fun builder() = Builder()
    }
} 