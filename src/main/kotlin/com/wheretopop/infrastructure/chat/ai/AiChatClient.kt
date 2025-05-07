package com.wheretopop.infrastructure.chat.ai

import com.wheretopop.shared.exception.ChatNullResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class ExperimentalStream

@Component
class AiChatClient(private val chatClient: ChatClient) {

    fun call(userPrompt: String): String =
        chatClient.prompt().user(userPrompt)
            .call().content() ?: throw ChatNullResponseException()

    fun call(systemPrompt: String?, userPrompt: String): String {
        val spec = chatClient.prompt().apply {
            systemPrompt?.let { system(it) }
            user(userPrompt)
        }
        return spec.call().content() ?: throw ChatNullResponseException()
    }

    fun call(prompt: Prompt): String =
        chatClient.prompt(prompt).call().content() ?: throw ChatNullResponseException()

    fun call(messages: List<Message>, options: ChatOptions? = null): String {
        val spec = chatClient.prompt().apply {
            messages(messages)
            options?.let { options(it) }
        }
        return spec.call().content() ?: throw ChatNullResponseException()
    }

    fun callWithResponse(userPrompt: String): ChatResponse =
        chatClient.prompt().user(userPrompt)
            .call().chatResponse() ?: throw ChatNullResponseException()

    fun callWithResponse(systemPrompt: String?, userPrompt: String): ChatResponse {
        val spec = chatClient.prompt().apply {
            systemPrompt?.let { system(it) }
            user(userPrompt)
        }
        return spec.call().chatResponse() ?: throw ChatNullResponseException()
    }

    fun callWithResponse(prompt: Prompt): ChatResponse =
        chatClient.prompt(prompt).call().chatResponse() ?: throw ChatNullResponseException()

    fun callWithResponse(messages: List<Message>, options: ChatOptions? = null): ChatResponse {
        val spec = chatClient.prompt().apply {
            messages(messages)
            options?.let { options(it) }
        }
        return spec.call().chatResponse() ?: throw ChatNullResponseException()
    }

    @ExperimentalStream
    suspend fun stream(userPrompt: String): Flow<ChatResponse> = flow {
        chatClient.prompt().user(userPrompt)
            .stream().chatResponse().asFlow().collect { emit(it) }
    }

    @ExperimentalStream
    suspend fun stream(systemPrompt: String?, userPrompt: String): Flow<ChatResponse> = flow {
        chatClient.prompt().apply {
            systemPrompt?.let { system(it) }
            user(userPrompt)
        }.stream().chatResponse().asFlow().collect { emit(it) }
    }

    @ExperimentalStream
    suspend fun stream(prompt: Prompt): Flow<ChatResponse> = flow {
        chatClient.prompt(prompt)
            .stream().chatResponse().asFlow().collect { emit(it) }
    }

    @ExperimentalStream
    suspend fun stream(messages: List<Message>, options: ChatOptions? = null): Flow<ChatResponse> = flow {
        chatClient.prompt().apply {
            messages(messages)
            options?.let { options(it) }
        }.stream().chatResponse().asFlow().collect { emit(it) }
    }
}
