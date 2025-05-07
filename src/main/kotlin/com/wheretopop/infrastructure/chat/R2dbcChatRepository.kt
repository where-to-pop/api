package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.infrastructure.chat.message.ChatMessageEntity
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository

@Repository
class R2dbcChatRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : ChatRepository {
    private val chatEntityClass = ChatEntity::class.java
    private val messageEntityClass = ChatMessageEntity::class.java

    override suspend fun findById(id: ChatId): Chat? {
        val chatEntity = entityTemplate
            .selectOne(query(where("id").`is`(id)), chatEntityClass)
            .awaitSingleOrNull() ?: return null
            
        val messages = findMessagesByChatId(id)
        return chatEntity.toDomain(messages)
    }

    override suspend fun findByUserId(userId: UserId): List<Chat> =
        entityTemplate
            .select(query(where("user_id").`is`(userId)), chatEntityClass)
            .collectList()
            .awaitSingle()
            .map { entity -> 
                val messages = findMessagesByChatId(entity.id)
                entity.toDomain(messages)
            }

    override suspend fun findByProjectId(projectId: ProjectId): List<Chat> =
        entityTemplate
            .select(query(where("project_id").`is`(projectId)), chatEntityClass)
            .collectList()
            .awaitSingle()
            .map { entity -> 
                val messages = findMessagesByChatId(entity.id)
                entity.toDomain(messages)
            }

    override suspend fun findAll(): List<Chat> =
        entityTemplate
            .select(chatEntityClass)
            .all()
            .collectList()
            .awaitSingle()
            .map { entity -> 
                val messages = findMessagesByChatId(entity.id)
                entity.toDomain(messages)
            }

    override suspend fun save(chat: Chat): Chat {
        val chatEntity = ChatEntity.of(chat)
        val exists = entityTemplate.exists(query(where("id").`is`(chat.id)), chatEntityClass).awaitSingle()
        
        if (exists) {
            entityTemplate.update(chatEntity).awaitSingle()
        } else {
            entityTemplate.insert(chatEntity).awaitSingle()
        }
        
        saveMessages(chat)
        return chat
    }

    override suspend fun save(chats: List<Chat>): List<Chat> =
        chats.map { save(it) }

    override suspend fun deleteById(id: ChatId) {
        entityTemplate
            .delete(query(where("id").`is`(id)), chatEntityClass)
            .awaitSingle()
        
        // 관련 메시지도 삭제
        entityTemplate
            .delete(query(where("chat_id").`is`(id)), messageEntityClass)
            .awaitSingle()
    }
    
    private suspend fun findMessagesByChatId(chatId: ChatId): List<ChatMessageEntity> =
        entityTemplate
            .select(query(where("chat_id").`is`(chatId)), messageEntityClass)
            .collectList()
            .awaitSingle()
    
    private suspend fun saveMessages(chat: Chat) {
        chat.messages.forEach { message ->
            val messageEntity = ChatMessageEntity.of(message)
            val exists = entityTemplate.exists(query(where("id").`is`(message.id)), messageEntityClass).awaitSingle()
            
            if (exists) {
                entityTemplate.update(messageEntity).awaitSingle()
            } else {
                entityTemplate.insert(messageEntity).awaitSingle()
            }
        }
    }
} 