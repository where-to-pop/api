package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId

/**
 * 채팅 도메인 객체에 대한 저장소 인터페이스
 */
interface ChatRepository {
    suspend fun findById(id: ChatId): Chat?
    suspend fun findByUserId(userId: UserId): List<Chat>
    suspend fun findByProjectId(projectId: ProjectId): List<Chat>
    suspend fun findAll(): List<Chat>
    suspend fun save(chat: Chat): Chat
    suspend fun save(chats: List<Chat>): List<Chat>
    suspend fun deleteById(id: ChatId)
}
