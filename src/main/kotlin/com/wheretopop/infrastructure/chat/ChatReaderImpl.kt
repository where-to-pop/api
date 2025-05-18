package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatReader
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import org.springframework.stereotype.Component

/**
 * ChatReader 인터페이스의 구현체
 * Repository 패턴을 통해 조회를 위임합니다.
 */
@Component
class ChatReaderImpl(
    private val chatRepository: ChatRepository
) : ChatReader {

    override fun findAll(): List<Chat> {
        return chatRepository.findAll()
    }

    override fun findById(id: ChatId): Chat? {
        return chatRepository.findById(id)
    }
    
    override fun findByUserId(userId: UserId): List<Chat> {
        return chatRepository.findByUserId(userId)
    }
    
    override fun findByProjectId(projectId: ProjectId): List<Chat> {
        return chatRepository.findByProjectId(projectId)
    }
} 