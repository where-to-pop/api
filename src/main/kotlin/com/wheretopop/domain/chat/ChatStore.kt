package com.wheretopop.domain.chat

interface ChatStore {
    fun save(chat: Chat): Chat
    fun save(chats: List<Chat>): List<Chat>
}