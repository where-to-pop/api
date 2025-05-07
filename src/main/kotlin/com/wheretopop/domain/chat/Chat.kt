package com.wheretopop.domain.chat

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import java.time.Instant

class Chat private constructor(
    val id: ChatId,
    val userId: UserId,
    val projectId: ProjectId,
    val isActive: Boolean,
    val title: String,
    val messages: List<ChatMessage>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
){
    companion object {
        fun create(
            id: ChatId,
            userId: UserId,
            projectId: ProjectId,
            isActive: Boolean,
            title: String,
            messages: List<ChatMessage>,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): Chat {
            return Chat(
                id,
                userId,
                projectId,
                isActive,
                title,
                messages,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
}