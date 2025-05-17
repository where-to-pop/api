package com.wheretopop.infrastructure.chat

import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions


interface ChatAssistant {

    suspend fun call(prompt: Prompt, toolCallingChatOption: ToolCallingChatOptions?): ChatResponse

}
