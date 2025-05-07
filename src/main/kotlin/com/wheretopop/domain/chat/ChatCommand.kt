package com.wheretopop.domain.chat

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import java.time.Instant

class ChatCommand {
    data class InitializeChat(
        val userId: UserId,
        val projectId: ProjectId,
        val isActive: Boolean,
        val title: String,
        val initialMessage: String,
    ){
        fun toDomain(): Chat {
            return Chat.create(
                userId = userId,
                projectId = projectId,
                isActive = isActive,
                title = title,
                messages = emptyList(),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null
            )
        }
    }


    data class UpdateChat(
        val chatId: ChatId,
        val title: String?,
        val isActive: Boolean?,
    )

    data class DeleteChat(
        val chatId: ChatId
    )
}