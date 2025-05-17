package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.JpaConverterConfig
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatMessage
import com.wheretopop.domain.chat.ChatMessageId
import com.wheretopop.shared.enums.ChatMessageFinishReason
import com.wheretopop.shared.enums.ChatMessageRole
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/**
 * 채팅 메시지(ChatMessage) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "chat_messages")
@EntityListeners(AuditingEntityListener::class)
class ChatMessageEntity(
    @Id
    @Convert(converter = JpaConverterConfig.ChatMessageIdConverter::class)
    val id: ChatMessageId,
    
    @Column(name = "chat_id", nullable = false)
    @Convert(converter = JpaConverterConfig.ChatIdConverter::class)
    val chatId: ChatId,
    
    @Column(nullable = false)
    val role: ChatMessageRole,
    
    @Column(nullable = false)
    val content: String,
    
    @Column(name = "finish_reason")
    val finishReason: ChatMessageFinishReason?,
    
    @Column(name = "latency_ms", nullable = false)
    val latencyMs: Long,
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
    
    @Column(name = "deleted_at")
    val deletedAt: Instant? = null
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
