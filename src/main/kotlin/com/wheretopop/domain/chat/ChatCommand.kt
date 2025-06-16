package com.wheretopop.domain.chat

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import java.time.Instant

class ChatCommand {
    data class InitializeChat(
        val userId: UserId,
        val projectId: ProjectId,
        val isActive: Boolean,
        val initialMessage: String,
        val context: String?
    ){
        fun toDomain(): Chat {
            return Chat.create(
                userId = userId,
                projectId = projectId,
                isActive = isActive,
                title = "새로운 채팅", // ai 응답을 통해 값 업데이트
                messages = emptyList(),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null
            )
        }
    }


    data class UpdateChat(
        val chatId: ChatId,
        val userId: UserId,
        val title: String?,
        val isActive: Boolean?,
    )

    data class DeleteChat(
        val chatId: ChatId
    )
}