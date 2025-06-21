package com.wheretopop.domain.chat

import com.wheretopop.shared.enums.ChatMessageFinishReason
import com.wheretopop.shared.enums.ChatMessageRole
import java.time.Instant

class ChatMessage private constructor(
    val id: ChatMessageId,
    val chatId: ChatId,
    val role: ChatMessageRole,
    val content: String,
    val finishReason: ChatMessageFinishReason?,
    val stepResult: String?,
    val latencyMs: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?
){
    companion object{
        fun create(
            id: ChatMessageId = ChatMessageId.create(),
            chatId: ChatId,
            role: ChatMessageRole,
            content: String,
            finishReason: ChatMessageFinishReason?,
            stepResult: String?,
            latencyMs: Long,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant?
        ): ChatMessage {
            return ChatMessage(
                id,
                chatId,
                role,
                content,
                finishReason,
                stepResult,
                latencyMs,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
}