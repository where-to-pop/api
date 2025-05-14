package com.wheretopop.interfaces.chat

import com.wheretopop.domain.chat.ChatInfo
import java.time.Instant

class ChatDto {
    data class InitializeRequest(
        val projectId: Long,
        val initialMessage: String
    )

    data class UpdateRequest(
        val title: String?
    )

    data class SendMessageRequest(
        val message: String
    )

    data class ChatResponse(
        val id: Long,
        val userId: Long,
        val projectId: Long,
        val isActive: Boolean,
        val title: String,
        val createdAt: Instant,
        val updatedAt: Instant
    ) {
        companion object {
            fun from(info: ChatInfo.Main): ChatResponse {
                return ChatResponse(
                    id = info.id,
                    userId = info.userId,
                    projectId = info.projectId,
                    isActive = info.isActive,
                    title = info.title,
                    createdAt = info.createdAt,
                    updatedAt = info.updatedAt
                )
            }
        }
    }

    data class ChatSimpleResponse(
        val id: Long,
        val userId: Long,
        val projectId: Long,
        val title: String,
        val latestUserMessage: MessageResponse?,
        val latestAssistantMessage: MessageResponse?
    ) {
        companion object {
            fun from(info: ChatInfo.Simple): ChatSimpleResponse {
                return ChatSimpleResponse(
                    id = info.id,
                    userId = info.userId,
                    projectId = info.projectId,
                    title = info.title,
                    latestUserMessage = info.latestUserMessage?.let { MessageResponse.from(it) },
                    latestAssistantMessage = info.latestAssistantMessage?.let { MessageResponse.from(it) }
                )
            }
        }
    }

    data class ChatDetailResponse(
        val id: Long,
        val userId: Long,
        val projectId: Long,
        val isActive: Boolean,
        val title: String,
        val messages: List<MessageResponse>,
        val createdAt: Instant,
        val updatedAt: Instant
    ) {
        companion object {
            fun from(info: ChatInfo.Detail): ChatDetailResponse {
                return ChatDetailResponse(
                    id = info.id,
                    userId = info.userId,
                    projectId = info.projectId,
                    isActive = info.isActive,
                    title = info.title,
                    messages = info.messages.map { MessageResponse.from(it) },
                    createdAt = info.createdAt,
                    updatedAt = info.updatedAt
                )
            }
        }
    }

    data class MessageResponse(
        val id: Long,
        val role: String,
        val content: String,
        val createdAt: Instant
    ) {
        companion object {
            fun from(info: ChatInfo.MessageInfo): MessageResponse {
                return MessageResponse(
                    id = info.id,
                    role = info.role,
                    content = info.content,
                    createdAt = info.createdAt
                )
            }
        }
    }
} 