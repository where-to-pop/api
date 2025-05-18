package com.wheretopop.domain.chat

import com.wheretopop.domain.user.UserId

interface ChatService {
    fun initializeChat(command: ChatCommand.InitializeChat): ChatInfo.Detail
    fun updateChat(command: ChatCommand.UpdateChat): ChatInfo.Main
    fun deleteChat(command: ChatCommand.DeleteChat): ChatInfo.Main
    fun sendMessage(chatId: ChatId, message: String): ChatInfo.Simple
    fun getDetail(chatId: ChatId): ChatInfo.Detail
    fun getList(userId: UserId): List<ChatInfo.Main>
}