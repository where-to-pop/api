package com.wheretopop.application.chat

import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatFacade(
//    private val chatService: ChatService
//    private val aiChatClient: AiChatClient
) {

    fun sendUserMessage(chatId: Long, userMessage: String): Mono<Void> {
        TODO()
//        return chatService.sendUserMessage(chatId, message)
    }

    fun streamChat(chatId: Long): Flux<ServerSentEvent<String>> {
        TODO()
//        return chatService.streamAssistantMessages(chatId)
    }


    suspend fun chat(userMessage: String): String {
        TODO()
    }
}
