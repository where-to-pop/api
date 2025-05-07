package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.infrastructure.chat.message.ChatMessageEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("chats")
internal class ChatEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: ChatId,
    @Column("user_id")
    val userId: UserId,
    @Column("project_id")
    val projectId: ProjectId,
    @Column("title")
    val title: String,
    @Column("is_active")
    val isActive: Boolean,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant?
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

@WritingConverter
class ChatIdToLongConverter : Converter<ChatId, Long> {
    override fun convert(source: ChatId) = source.toLong()
}

@ReadingConverter
class LongToChatIdConverter : Converter<Long, ChatId> {
    override fun convert(source: Long) = ChatId.of(source)
}