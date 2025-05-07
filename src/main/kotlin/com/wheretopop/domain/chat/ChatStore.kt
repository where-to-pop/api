package com.wheretopop.domain.chat

interface ChatStore {
    suspend fun save(chat: Chat): Chat
    suspend fun save(chats: List<Chat>): List<Chat>
}