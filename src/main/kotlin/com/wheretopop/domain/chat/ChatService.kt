package com.wheretopop.domain.chat

import com.wheretopop.domain.user.UserId
import kotlinx.coroutines.flow.SharedFlow

interface ChatService {
    fun initializeChat(command: ChatCommand.InitializeChat): ChatInfo.Detail
    fun updateChat(command: ChatCommand.UpdateChat): ChatInfo.Main
    fun deleteChat(command: ChatCommand.DeleteChat): ChatInfo.Main

    fun sendMessage(chatId: ChatId, message: String, context: String?): ChatInfo.Simple
    fun getChatExecutionStatusStream(chatId: ChatId, userId: UserId, executionId: String?): SharedFlow<String>

    fun getSimple(chatId: ChatId): ChatInfo.Simple
    fun getDetail(chatId: ChatId): ChatInfo.Detail
    fun getList(userId: UserId): List<ChatInfo.Main>
}

