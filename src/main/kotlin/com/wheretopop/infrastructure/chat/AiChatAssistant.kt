package com.wheretopop.infrastructure.chat

import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class AiChatAssistant(private val chatClient: ChatClient) : ChatAssistant {

    override suspend fun call(userPrompt: String): String {
        logger.info { "call(userPrompt: String) - User prompt: $userPrompt" }
        logger.debug { "Calling AI model..." }
        val response = chatClient.prompt().user(userPrompt)
            .call().content()
        logger.debug { "Received AI response." }
        return response ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    override suspend fun call(systemPrompt: String?, userPrompt: String): String {
        logger.info { "call(systemPrompt: String?, userPrompt: String) - System prompt: $systemPrompt, User prompt: $userPrompt" }
        val spec = chatClient.prompt().apply {
            systemPrompt?.let { system(it) }
            user(userPrompt)
        }
        logger.debug { "Calling AI model..." }
        val response = spec.call().content()
        logger.debug { "Received AI response." }
        return response ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    override suspend fun call(prompt: Prompt): String {
        logger.info { "call(prompt: Prompt) - Prompt contents: ${prompt.contents}" }
        logger.debug { "Calling AI model..." }
        val response = chatClient.prompt(prompt).call().content()
        logger.debug { "Received AI response." }
        return response ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    override suspend fun call(messages: List<Message>, options: ChatOptions?): String {
        logger.info { "call(messages: List<Message>, options: ChatOptions?) - Messages count: ${messages.size}, Options: $options" }
        val spec = chatClient.prompt().apply {
            messages(messages)
            options?.let { options(it) }
        }
        logger.debug { "Calling AI model..." }
        val response = spec.call().content()
        logger.debug { "Received AI response." }
        return response ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    override suspend fun callWithResponse(userPrompt: String): ChatResponse {
        logger.info { "callWithResponse(userPrompt: String) - User prompt: $userPrompt" }
        logger.debug { "Calling AI model for ChatResponse..." }
        val chatResponse = chatClient.prompt().user(userPrompt)
            .call().chatResponse()
        logger.debug { "Received AI ChatResponse." }
        return chatResponse ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    override suspend fun callWithResponse(systemPrompt: String?, userPrompt: String): ChatResponse {
        logger.info { "callWithResponse(systemPrompt: String?, userPrompt: String) - System prompt: $systemPrompt, User prompt: $userPrompt" }
        val spec = chatClient.prompt().apply {
            systemPrompt?.let { system(it) }
            user(userPrompt)
        }
        logger.debug { "Calling AI model for ChatResponse..." }
        val chatResponse = spec.call().chatResponse()
        logger.debug { "Received AI ChatResponse." }
        return chatResponse ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    override suspend fun callWithResponse(prompt: Prompt): ChatResponse {
        logger.info { "callWithResponse(prompt: Prompt) - Prompt contents: ${prompt.contents}" }
        logger.debug { "Calling AI model for ChatResponse..." }
        val chatResponse = chatClient.prompt(prompt).call().chatResponse()
        logger.debug { "Received AI ChatResponse." }
        return chatResponse ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    override suspend fun callWithResponse(messages: List<Message>, options: ChatOptions?): ChatResponse {
        logger.info { "callWithResponse(messages: List<Message>, options: ChatOptions?) - Messages count: ${messages.size}, Options: $options" }
        val spec = chatClient.prompt().apply {
            messages(messages)
            options?.let { options(it) }
        }
        logger.debug { "Calling AI model for ChatResponse..." }
        val chatResponse = spec.call().chatResponse()
        logger.debug { "Received AI ChatResponse." }
        return chatResponse ?: throw ErrorCode.CHAT_NULL_RESPONSE.toException()
    }

    @ExperimentalStream
    override suspend fun stream(userPrompt: String): Flow<ChatResponse> {
        logger.info { "stream(userPrompt: String) - User prompt: $userPrompt" }
        return chatClient.prompt().user(userPrompt)
            .stream().chatResponse().asFlow()
            .onStart { logger.debug { "AI stream started for user prompt: $userPrompt" } }
            .onCompletion { logger.debug { "AI stream completed for user prompt: $userPrompt" } }
    }

    @ExperimentalStream
    override suspend fun stream(systemPrompt: String?, userPrompt: String): Flow<ChatResponse> {
        logger.info { "stream(systemPrompt: String?, userPrompt: String) - System prompt: $systemPrompt, User prompt: $userPrompt" }
        return chatClient.prompt().apply {
            systemPrompt?.let { system(it) }
            user(userPrompt)
        }.stream().chatResponse().asFlow()
            .onStart { logger.debug { "AI stream started for user prompt: $userPrompt" } }
            .onCompletion { logger.debug { "AI stream completed for user prompt: $userPrompt" } }
    }

    @ExperimentalStream
    override suspend fun stream(prompt: Prompt): Flow<ChatResponse> {
        logger.info { "stream(prompt: Prompt) - Prompt contents: ${prompt.contents}" }
        return chatClient.prompt(prompt)
            .stream().chatResponse().asFlow()
            .onStart { logger.debug { "AI stream started for prompt: ${prompt.contents}" } }
            .onCompletion { logger.debug { "AI stream completed for prompt: ${prompt.contents}" } }
    }

    @ExperimentalStream
    override suspend fun stream(messages: List<Message>, options: ChatOptions?): Flow<ChatResponse> {
        logger.info { "stream(messages: List<Message>, options: ChatOptions?) - Messages count: ${messages.size}, Options: $options" }
        return chatClient.prompt().apply {
            messages(messages)
            options?.let { options(it) }
        }.stream().chatResponse().asFlow()
            .onStart { logger.debug { "AI stream started for messages count: ${messages.size}" } }
            .onCompletion { logger.debug { "AI stream completed for messages count: ${messages.size}" } }
    }
}
