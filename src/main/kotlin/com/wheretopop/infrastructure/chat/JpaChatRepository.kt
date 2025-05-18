package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.infrastructure.entity.ChatMessageEntity
import com.wheretopop.shared.infrastructure.entity.ChatEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class JpaChatRepository(
    @PersistenceContext private val entityManager: EntityManager
) : ChatRepository {

    override fun findById(id: ChatId): Chat? {
        val chatEntity = entityManager.find(ChatEntity::class.java, id) ?: return null
        val messages = findMessagesByChatId(id)
        return chatEntity.toDomain(messages)
    }

    override fun findByUserId(userId: UserId): List<Chat> {
        val query = entityManager.createQuery(
            "SELECT c FROM ChatEntity c WHERE c.userId = :userId AND c.deletedAt IS NULL", 
            ChatEntity::class.java
        )
        query.setParameter("userId", userId)
        
        return query.resultList.map { entity -> 
            val messages = findMessagesByChatId(entity.id)
            entity.toDomain(messages)
        }
    }

    override fun findByProjectId(projectId: ProjectId): List<Chat> {
        val query = entityManager.createQuery(
            "SELECT c FROM ChatEntity c WHERE c.projectId = :projectId AND c.deletedAt IS NULL", 
            ChatEntity::class.java
        )
        query.setParameter("projectId", projectId)
        
        return query.resultList.map { entity -> 
            val messages = findMessagesByChatId(entity.id)
            entity.toDomain(messages)
        }
    }

    override fun findAll(): List<Chat> {
        val query = entityManager.createQuery(
            "SELECT c FROM ChatEntity c WHERE c.deletedAt IS NULL", 
            ChatEntity::class.java
        )
        
        return query.resultList.map { entity -> 
            val messages = findMessagesByChatId(entity.id)
            entity.toDomain(messages)
        }
    }

    override fun save(chat: Chat): Chat {
        val chatEntity = ChatEntity.of(chat)
        entityManager.persist(chatEntity)
        saveMessages(chat)
        return chat
    }

    override fun save(chats: List<Chat>): List<Chat> =
        chats.map { save(it) }
    
    private fun findMessagesByChatId(chatId: ChatId): List<ChatMessageEntity> {
        val query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatId = :chatId AND m.deletedAt IS NULL", 
            ChatMessageEntity::class.java
        )
        query.setParameter("chatId", chatId)
        return query.resultList
    }
    
    private fun saveMessages(chat: Chat) {
        chat.messages.forEach { message ->
            val messageEntity = ChatMessageEntity.of(message)
            entityManager.persist(messageEntity)
        }
    }
} 