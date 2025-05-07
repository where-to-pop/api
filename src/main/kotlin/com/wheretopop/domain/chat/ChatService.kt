package com.wheretopop.domain.chat

import com.wheretopop.domain.user.UserId

interface ChatService {
    suspend fun initializeChat(command: ChatCommand.InitializeChat): ChatInfo.Detail
    suspend fun updateChat(command: ChatCommand.UpdateChat): ChatInfo.Main
    suspend fun deleteChat(command: ChatCommand.DeleteChat): ChatInfo.Main
    suspend fun sendMessage(chatId: ChatId, message: String): ChatInfo.Simple
    suspend fun getDetail(chatId: ChatId): ChatInfo.Detail
    suspend fun getList(userId: UserId): List<ChatInfo.Main>
}