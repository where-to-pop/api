package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.JpaConverterConfig
import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/**
 * 채팅(Chat) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "chats")
@EntityListeners(AuditingEntityListener::class)
class ChatEntity(
    @Id
    @Convert(converter = JpaConverterConfig.ChatIdConverter::class)
    val id: ChatId,
    
    @Column(name = "user_id", nullable = false)
    @Convert(converter = JpaConverterConfig.UserIdConverter::class)
    val userId: UserId,
    
    @Column(name = "project_id", nullable = false)
    @Convert(converter = JpaConverterConfig.ProjectIdConverter::class)
    val projectId: ProjectId,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean,
    
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
        fun of(chat: Chat): ChatEntity {
            return ChatEntity(
                id = chat.id,
                userId = chat.userId,
                projectId = chat.projectId,
                title = chat.title,
                isActive = chat.isActive,
                createdAt = chat.createdAt,
                updatedAt = chat.updatedAt,
                deletedAt = chat.deletedAt
            )
        }
    }

    fun toDomain(chatMessages: List<ChatMessageEntity>): Chat {
        return Chat.create(
            id = id,
            title = title,
            userId = userId,
            projectId = projectId,
            isActive = isActive,
            messages = chatMessages.map { it.toDomain() },
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }

    fun update(chat: Chat): ChatEntity {
        return ChatEntity(
            id = id,
            title = chat.title,
            userId = userId,
            projectId = projectId,
            isActive = chat.isActive,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
}