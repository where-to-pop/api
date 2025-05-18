package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.infrastructure.entity.ChatEntity
import com.wheretopop.shared.infrastructure.entity.ChatMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * JPA 채팅 저장소 인터페이스
 */
@Repository
interface JpaChatRepository : JpaRepository<ChatEntity, Long> {
    fun findByUserId(userId: Long): List<ChatEntity>
    fun findByProjectId(projectId: Long): List<ChatEntity>
}

/**
 * JPA 채팅 메시지 저장소 인터페이스
 */
@Repository
interface JpaChatMessageRepository : JpaRepository<ChatMessageEntity, Long> {
    @Query("SELECT m FROM ChatMessageEntity m WHERE m.chatId = :chatId AND m.deletedAt IS NULL")
    fun findByChatId(@Param("chatId") chatId: Long): List<ChatMessageEntity>
}

/**
 * 채팅 저장소 JPA 구현체
 */
@Repository
class ChatRepositoryJpaAdapter(
    private val chatRepository: JpaChatRepository,
    private val chatMessageRepository: JpaChatMessageRepository
) : ChatRepository {

    override fun findById(id: ChatId): Chat? {
        val chatEntity = chatRepository.findById(id.toLong()).orElse(null) ?: return null
        val messages = chatMessageRepository.findByChatId(chatEntity.id)
        return chatEntity.toDomain(messages)
    }

    override fun findByUserId(userId: UserId): List<Chat> {
        return chatRepository.findByUserId(userId.toLong()).map { entity ->
            val messages = chatMessageRepository.findByChatId(entity.id)
            entity.toDomain(messages)
        }
    }

    override fun findByProjectId(projectId: ProjectId): List<Chat> {
        return chatRepository.findByProjectId(projectId.toLong()).map { entity ->
            val messages = chatMessageRepository.findByChatId(entity.id)
            entity.toDomain(messages)
        }
    }

    override fun findAll(): List<Chat> {
        return chatRepository.findAll().map { entity ->
            val messages = chatMessageRepository.findByChatId(entity.id)
            entity.toDomain(messages)
        }
    }

    override fun save(chat: Chat): Chat {
        val chatEntity = ChatEntity.of(chat)
        val savedChatEntity = chatRepository.save(chatEntity)
        
        // 메시지 저장
        chat.messages.forEach { message ->
            val messageEntity = ChatMessageEntity.of(message)
            chatMessageRepository.save(messageEntity)
        }
        
        return savedChatEntity.toDomain(
            chatMessageRepository.findByChatId(savedChatEntity.id)
        )
    }

    override fun save(chats: List<Chat>): List<Chat> = 
        chats.map { save(it) }
} 