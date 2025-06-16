package com.wheretopop.domain.chat

import com.wheretopop.infrastructure.chat.prompt.ReActStreamResponse
import kotlinx.coroutines.flow.Flow

interface ChatScenario {
    fun generateTitle(chat: Chat): String
    fun processUserMessageStream(chat: Chat, context: String?): Flow<ReActStreamResponse>
}
