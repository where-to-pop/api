package com.wheretopop.domain.chat

interface ChatScenario {
    fun generateTitle(chat: Chat): String
    fun processUserMessage(chat: Chat): Chat
}
