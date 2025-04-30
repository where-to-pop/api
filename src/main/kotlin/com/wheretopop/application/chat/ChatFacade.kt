package com.wheretopop.application.chat

import com.wheretopop.infrastructure.chat.ai.AiChatClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import org.springframework.ai.chat.model.Generation
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatFacade(
//    private val chatService: ChatService
    private val aiChatClient: AiChatClient
) {

    fun sendUserMessage(chatId: Long, userMessage: String): Mono<Void> {
        TODO()
//        return chatService.sendUserMessage(chatId, message)
    }

    fun streamChat(chatId: Long): Flux<ServerSentEvent<String>> {
        TODO()
//        return chatService.streamAssistantMessages(chatId)
    }


    suspend fun chat(userMessage: String): String =
        // DSL 블록으로 call 호출
        aiChatClient.call(userMessage)

    suspend fun streamGenerations(userMessage: String): Flow<Generation> =
        // DSL 블록으로 stream 호출
        aiChatClient.stream(userMessage).flatMapConcat {
            it.results.asFlow()
        }


}
