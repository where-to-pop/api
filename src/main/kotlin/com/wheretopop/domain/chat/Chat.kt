package com.wheretopop.domain.chat

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.ChatMessageRole
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
    val deletedAt: Instant?
){
    companion object {
        fun create(
            id: ChatId = ChatId.create(),
            userId: UserId,
            projectId: ProjectId,
            isActive: Boolean,
            title: String,
            messages: List<ChatMessage>,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant?
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

    /**
     * 새로운 메시지를 추가한 새 Chat 객체를 반환합니다.
     */
    fun addMessage(message: ChatMessage): Chat {
        val updatedMessages = messages.toMutableList().apply {
            add(message)
        }
        
        return Chat(
            id = id,
            userId = userId,
            projectId = projectId,
            isActive = isActive,
            title = title,
            messages = updatedMessages,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
    
    /**
     * 채팅 정보를 업데이트한 새 Chat 객체를 반환합니다.
     */
    fun update(title: String? = null, isActive: Boolean? = null): Chat {
        return Chat(
            id = id,
            userId = userId,
            projectId = projectId,
            isActive = isActive ?: this.isActive,
            title = title ?: this.title,
            messages = messages,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
    
    /**
     * 채팅을 삭제한 새 Chat 객체를 반환합니다.
     */
    fun delete(): Chat {
        return Chat(
            id = id,
            userId = userId,
            projectId = projectId,
            isActive = false,
            title = title,
            messages = messages,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = Instant.now()
        )
    }
    
    /**
     * 가장 최근 메시지를 반환합니다. 메시지가 없으면 null을 반환합니다.
     */
    fun getLatestMessage(): ChatMessage? {
        return if (messages.isEmpty()) null else messages.last()
    }
    
    /**
     * 최근 사용자 메시지를 반환합니다. 없으면 null을 반환합니다.
     */
    fun getLatestUserMessage(): ChatMessage? {
        return messages.lastOrNull { it.role == ChatMessageRole.USER }
    }
    
    /**
     * 최근 어시스턴트 메시지를 반환합니다. 없으면 null을 반환합니다.
     */
    fun getLatestAssistantMessage(): ChatMessage? {
        return messages.lastOrNull { it.role == ChatMessageRole.ASSISTANT }
    }
    
    /**
     * 채팅 내 모든 시스템 메시지를, 없으면 빈 리스트를 반환합니다.
     */
    fun getSystemMessages(): List<ChatMessage> {
        return messages.filter { it.role == ChatMessageRole.SYSTEM }
    }
    
    /**
     * 최근 N개의 메시지를 반환합니다 (최신 순).
     * 전체 메시지 수가 N보다 적으면 모든 메시지를 반환합니다.
     */
    fun getRecentMessages(count: Int): List<ChatMessage> {
        if (count <= 0) return emptyList()
        return if (messages.size <= count) {
            messages
        } else {
            messages.takeLast(count)
        }
    }
    
    /**
     * 최근 N개의 메시지를 하나의 컨텍스트 문자열로 결합하여 반환합니다.
     * 형식: "USER: 메시지내용\nASSISTANT: 응답내용\n..."
     */
    fun getRecentMessagesAsContext(count: Int): String {
        return getRecentMessages(count).joinToString("\n") { message ->
            "${message.role.name}: ${message.content}"
        }
    }
}