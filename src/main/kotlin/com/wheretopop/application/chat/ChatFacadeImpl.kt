package com.wheretopop.application.chat

import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatInfo
import com.wheretopop.domain.chat.ChatService
import com.wheretopop.domain.user.UserId
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ChatFacadeImpl(
    private val chatService: ChatService
) : ChatFacade {
    
    override fun initialize(input: ChatInput.Initialize): ChatInfo.Detail {
        return chatService.initializeChat(input.toCommand())
    }

    override fun update(input: ChatInput.Update): ChatInfo.Main {
        return chatService.updateChat(input.toCommand())
    }

    override fun delete(input: ChatInput.Delete): ChatInfo.Main {
        return chatService.deleteChat(input.toCommand())
    }

    override fun sendMessage(input: ChatInput.SendMessage): ChatInfo.Simple {
        return chatService.sendMessage(input.chatId, input.message)
    }

    override fun getChatExecutionStatusStream(chatId: ChatId, userId: UserId, executionId: String?): Flow<String> {
        return chatService.getChatExecutionStatusStream(chatId, userId, executionId)
    }

    override fun getDetail(chatId: ChatId): ChatInfo.Detail {
        return chatService.getDetail(chatId)
    }

    override fun getList(userId: UserId): List<ChatInfo.Main> {
        return chatService.getList(userId)
    }
} 