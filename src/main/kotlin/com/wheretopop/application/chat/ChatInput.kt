package com.wheretopop.application.chat

import com.wheretopop.domain.chat.ChatCommand
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId

class ChatInput {
    data class Initialize(
        val userId: UserId,
        val projectId: ProjectId,
        val initialMessage: String
    ) {
        fun toCommand() = ChatCommand.InitializeChat(
            userId = userId,
            projectId = projectId,
            initialMessage = initialMessage,
            isActive = true
        )
    }

    data class Update(
        val chatId: ChatId,
        val title: String?
    ) {
        fun toCommand() = ChatCommand.UpdateChat(
            chatId = chatId,
            title = title,
            isActive = null
        )
    }

    data class Delete(
        val chatId: ChatId
    ) {
        fun toCommand() = ChatCommand.DeleteChat(
            chatId = chatId
        )
    }

    data class SendMessage(
        val chatId: ChatId,
        val userId: UserId,
        val message: String
    )
} 