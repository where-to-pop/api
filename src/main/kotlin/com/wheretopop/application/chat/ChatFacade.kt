package com.wheretopop.application.chat

import com.wheretopop.domain.chat.ChatService
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatFacade(
    private val chatService: ChatService
) {

    fun sendUserMessage(chatId: Long, userMessage: String): Mono<Void> {
        TODO()
//        return chatService.sendUserMessage(chatId, message)
    }

    fun streamChat(chatId: Long): Flux<ServerSentEvent<String>> {
        TODO()
//        return chatService.streamAssistantMessages(chatId)
    }
}
