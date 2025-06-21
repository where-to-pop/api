package com.wheretopop.interfaces.chat

import com.wheretopop.domain.chat.ChatInfo
import java.time.Instant

class ChatDto {
    data class InitializeRequest(
        val projectId: String,
        val initialMessage: String
    )

    data class UpdateRequest(
        val title: String?
    )

    data class SendMessageRequest(
        val message: String
    )

    data class ChatResponse(
        val id: String,
        val userId: String,
        val projectId: String,
        val isActive: Boolean,
        val title: String,
        val createdAt: Instant,
        val updatedAt: Instant
    ) {
        companion object {
            fun from(info: ChatInfo.Main): ChatResponse {
                return ChatResponse(
                    id = info.id.toString(),
                    userId = info.userId.toString(),
                    projectId = info.projectId.toString(),
                    isActive = info.isActive,
                    title = info.title,
                    createdAt = info.createdAt,
                    updatedAt = info.updatedAt
                )
            }
        }
    }

    data class ChatSimpleResponse(
        val id: String,
        val userId: String,
        val projectId: String,
        val title: String,
        val latestUserMessage: MessageResponse?,
        val latestAssistantMessage: MessageResponse?
    ) {
        companion object {
            fun from(info: ChatInfo.Simple): ChatSimpleResponse {
                return ChatSimpleResponse(
                    id = info.id.toString(),
                    userId = info.userId.toString(),
                    projectId = info.projectId.toString(),
                    title = info.title,
                    latestUserMessage = info.latestUserMessage?.let { MessageResponse.from(it) },
                    latestAssistantMessage = info.latestAssistantMessage?.let { MessageResponse.from(it) }
                )
            }
        }
    }

    data class ChatDetailResponse(
        val id: String,
        val userId: String,
        val projectId: String,
        val isActive: Boolean,
        val title: String,
        val messages: List<MessageResponse>,
        val createdAt: Instant,
        val updatedAt: Instant
    ) {
        companion object {
            fun from(info: ChatInfo.Detail): ChatDetailResponse {
                return ChatDetailResponse(
                    id = info.id.toString(),
                    userId = info.userId.toString(),
                    projectId = info.projectId.toString(),
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
        val id: String,
        val role: String,
        val content: String,
        val stepResult: String?,
        val createdAt: Instant
    ) {
        companion object {
            fun from(info: ChatInfo.MessageInfo): MessageResponse {
                return MessageResponse(
                    id = info.id.toString(),
                    role = info.role,
                    content = info.content,
                    stepResult = info.stepResult,
                    createdAt = info.createdAt
                )
            }
        }
    }
} 