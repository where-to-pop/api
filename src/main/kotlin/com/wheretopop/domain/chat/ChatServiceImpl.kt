package com.wheretopop.domain.chat

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import org.springframework.stereotype.Service

/**
 * ChatService 인터페이스 구현체
 * 도메인 서비스와 인프라를 조율하여 애플리케이션 기능을 제공합니다.
 */
@Service
class ChatServiceImpl(
    private val chatReader: ChatReader,
    private val chatStore: ChatStore,
    private val chatCoordinator: ChatCoordinator
): ChatService {

    /**
     * 새 채팅을 초기화합니다.
     */
    override suspend fun initializeChat(command: ChatCommand.InitializeChat): ChatInfo.Detail {
        val chat = command.toDomain()
        val chatWithResponse = chatCoordinator.processUserMessage(chat, command.initialMessage)
        val title = chatCoordinator.summarizeTitle(chatWithResponse)
        val updatedChat = chatWithResponse.update(title = title)
        val savedChat = chatStore.save(updatedChat)
        return ChatInfoMapper.toDetailInfo(savedChat)
    }

    /**
     * 채팅 정보를 업데이트합니다.
     */
    override suspend fun updateChat(command: ChatCommand.UpdateChat): ChatInfo.Main {
        val (chatId, title, isActive) = command
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        val updatedChat = chat.update(title, isActive)
        val savedChat = chatStore.save(updatedChat)
        return ChatInfoMapper.toMainInfo(savedChat)
    }

    /**
     * 채팅을 삭제(소프트 삭제)합니다.
     */
    override suspend fun deleteChat(command: ChatCommand.DeleteChat): ChatInfo.Main {
        val (chatId) = command
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        
        val deletedChat = chat.delete()
        val savedChat = chatStore.save(deletedChat)
        
        return ChatInfoMapper.toMainInfo(savedChat)
    }
    
    /**
     * 채팅에 사용자 메시지를 추가하고 AI 응답을 반환합니다.
     */
    override suspend fun sendMessage(chatId: ChatId, message: String): ChatInfo.Simple {
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        
        val updatedChat = chatCoordinator.processUserMessage(chat, message)
        val savedChat = chatStore.save(updatedChat)
        
        return ChatInfoMapper.toSimpleInfo(savedChat)
    }
    
    /**
     * 채팅의 상세 정보를 조회합니다.
     */
    override suspend fun getDetail(chatId: ChatId): ChatInfo.Detail {
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        return ChatInfoMapper.toDetailInfo(chat)
    }
    
    /**
     * 채팅의 기본 정보 목록을 조회합니다.
     */
    override suspend fun getList(userId: UserId): List<ChatInfo.Main> {
        val chats = chatReader.findByUserId(userId)
        return chats.map { ChatInfoMapper.toMainInfo(it) }
    }
}