package com.wheretopop.domain.chat

import com.wheretopop.shared.enums.ChatMessageAuthor
import java.time.Instant

class ChatMessage private  constructor(
    chatMessageId: ChatMessageId,
    author: ChatMessageAuthor,
    content: String,
    createdAt: Instant,
    updatedAt: Instant,
    deletedAt: Instant? = null
){
    companion object{
        fun create(
            chatMessageId: ChatMessageId,
            author: ChatMessageAuthor,
            content: String,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): ChatMessage {
            return ChatMessage(
                chatMessageId,
                author,
                content,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
}