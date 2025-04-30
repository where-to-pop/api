package com.wheretopop.domain.chat

import java.time.Instant

class Chat private constructor(
    id: ChatId,
    // userId: UserId
    messages: List<ChatMessage>,
    createdAt: Instant,
    updatedAt: Instant,
    deletedAt: Instant? = null
){
    companion object {
        fun create(
            id: ChatId,
            messages: List<ChatMessage>,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): Chat {
            return Chat(
                id,
                messages,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
}