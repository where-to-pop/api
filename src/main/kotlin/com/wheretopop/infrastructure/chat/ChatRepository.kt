package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import org.springframework.stereotype.Repository

/**
 * 채팅 도메인 객체에 대한 저장소 인터페이스
 */
@Repository
interface ChatRepository {
    fun findById(id: ChatId): Chat?
    fun findByUserId(userId: UserId): List<Chat>
    fun findByProjectId(projectId: ProjectId): List<Chat>
    fun findAll(): List<Chat>
    fun save(chat: Chat): Chat
    fun save(chats: List<Chat>): List<Chat>
}
