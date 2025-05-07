package com.wheretopop.infrastructure.chat

import kotlinx.coroutines.flow.Flow
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class ExperimentalStream


interface ChatAssistant {
    suspend fun call(userPrompt: String): String
    suspend fun call(systemPrompt: String?, userPrompt: String): String
    suspend fun call(prompt: Prompt): String
    suspend fun call(messages: List<Message>, options: ChatOptions? = null): String

    suspend fun callWithResponse(userPrompt: String): ChatResponse
    suspend fun callWithResponse(systemPrompt: String?, userPrompt: String): ChatResponse
    suspend fun callWithResponse(prompt: Prompt): ChatResponse
    suspend fun callWithResponse(messages: List<Message>, options: ChatOptions? = null): ChatResponse

    @ExperimentalStream
    suspend fun stream(userPrompt: String): Flow<ChatResponse>
    @ExperimentalStream
    suspend fun stream(systemPrompt: String?, userPrompt: String): Flow<ChatResponse>
    @ExperimentalStream
    suspend fun stream(prompt: Prompt): Flow<ChatResponse>
    @ExperimentalStream
    suspend fun stream(messages: List<Message>, options: ChatOptions? = null): Flow<ChatResponse>
}
