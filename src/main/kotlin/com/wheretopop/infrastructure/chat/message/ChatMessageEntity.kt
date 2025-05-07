package com.wheretopop.infrastructure.chat.message

import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatMessage
import com.wheretopop.domain.chat.ChatMessageId
import com.wheretopop.shared.enums.ChatMessageFinishReason
import com.wheretopop.shared.enums.ChatMessageRole
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("chat_messages")
internal class ChatMessageEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: ChatMessageId,
    @Column("chat_id")
    val chatId: ChatId,
    @Column("role")
    val role: ChatMessageRole,
    @Column("content")
    val content: String,
    @Column("finish_reason")
    val finishReason: ChatMessageFinishReason,
    @Column("latency_ms")
    val latencyMs: Long,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant?
) {
    companion object {
        fun of(chatMessage: ChatMessage): ChatMessageEntity {
            return ChatMessageEntity(
                id = chatMessage.id,
                chatId = chatMessage.chatId,
                role = chatMessage.role,
                content = chatMessage.content,
                finishReason = chatMessage.finishReason,
                latencyMs = chatMessage.latencyMs,
                createdAt = chatMessage.createdAt,
                updatedAt = chatMessage.updatedAt,
                deletedAt = chatMessage.deletedAt
            )
        }
    }

    fun toDomain(): ChatMessage {
        return ChatMessage.create(
            id = id,
            chatId = chatId,
            role = role,
            content = content,
            finishReason = finishReason,
            latencyMs = latencyMs,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }

    fun update(chatMessage: ChatMessage): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            chatId = chatId,
            role = chatMessage.role,
            content = chatMessage.content,
            finishReason = chatMessage.finishReason,
            latencyMs = chatMessage.latencyMs,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = chatMessage.deletedAt
        )
    }
}


@WritingConverter
class ChatMessageIdToLongConverter : Converter<ChatMessageId, Long> {
    override fun convert(source: ChatMessageId) = source.toLong()
}

@ReadingConverter
class LongToChatMessageIdConverter : Converter<Long, ChatMessageId> {
    override fun convert(source: Long) = ChatMessageId.of(source)
}
