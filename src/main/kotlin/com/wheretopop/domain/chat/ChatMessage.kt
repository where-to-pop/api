package com.wheretopop.domain.chat

import com.wheretopop.shared.enums.ChatMessageFinishReason
import com.wheretopop.shared.enums.ChatMessageRole
import java.time.Instant

class ChatMessage private constructor(
    val id: ChatMessageId,
    val chatId: ChatId,
    val role: ChatMessageRole,
    val content: String,
    val finishReason: ChatMessageFinishReason,
    val latencyMs: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
){
    companion object{
        fun create(
            id: ChatMessageId,
            chatId: ChatId,
            role: ChatMessageRole,
            content: String,
            finishReason: ChatMessageFinishReason,
            latencyMs: Long,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): ChatMessage {
            return ChatMessage(
                id,
                chatId,
                role,
                content,
                finishReason,
                latencyMs,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
}