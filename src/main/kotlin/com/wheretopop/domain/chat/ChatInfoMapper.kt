package com.wheretopop.domain.chat

/**
 * Chat 도메인 객체를 ChatInfo DTO로 변환하는 매퍼 클래스
 * 이 클래스는 도메인 모델과 DTO 사이의 변환을 담당합니다.
 */
class ChatInfoMapper {
    companion object {
        /**
         * Chat 도메인 객체를 ChatInfo.Main 으로 변환
         */
        fun toMainInfo(chat: Chat): ChatInfo.Main {
            return ChatInfo.Main(
                id = chat.id.toLong(),
                userId = chat.userId.toLong(),
                projectId = chat.projectId.toLong(),
                isActive = chat.isActive,
                title = chat.title,
                createdAt = chat.createdAt,
                updatedAt = chat.updatedAt
            )
        }

        /**
         * Chat 도메인 객체를 ChatInfo.Detail 로 변환
         */
        fun toDetailInfo(chat: Chat): ChatInfo.Detail {
            return ChatInfo.Detail(
                id = chat.id.toLong(),
                userId = chat.userId.toLong(),
                projectId = chat.projectId.toLong(),
                isActive = chat.isActive,
                title = chat.title,
                messages = chat.messages.map { toMessageInfo(it) },
                createdAt = chat.createdAt,
                updatedAt = chat.updatedAt
            )
        }

        /**
         * Chat 도메인 객체를 ChatInfo.Simple 로 변환
         */
        fun toSimpleInfo(chat: Chat): ChatInfo.Simple {
            val latestUserMessage = chat.getLatestUserMessage()?.let { toMessageInfo(it) }
            val latestAssistantMessage = chat.getLatestAssistantMessage()?.let { toMessageInfo(it) }

            return ChatInfo.Simple(
                id = chat.id.toLong(),
                userId = chat.userId.toLong(),
                projectId = chat.projectId.toLong(),
                title = chat.title,
                latestUserMessage = latestUserMessage,
                latestAssistantMessage = latestAssistantMessage
            )
        }

        /**
         * ChatMessage 도메인 객체를 ChatInfo.MessageInfo로 변환
         */
        private fun toMessageInfo(message: ChatMessage): ChatInfo.MessageInfo {
            return ChatInfo.MessageInfo(
                id = message.id.toLong(),
                role = message.role.name,
                content = message.content,
                createdAt = message.createdAt
            )
        }
    }
} 