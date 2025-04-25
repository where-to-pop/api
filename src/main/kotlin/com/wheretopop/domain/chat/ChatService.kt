package com.wheretopop.domain.chat

import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatService {
    fun sendUserMessage(chatId: Long, userMessage: String): Mono<Void>;
    fun generateAndSaveAssistantMessage(chatId: Long): Mono<Void>;
    fun streamAssistantMessages(chatId: Long): Flux<ServerSentEvent<String>>;
//    fun getChat(chatId: Long): Mono<Chat>;
//    fun getChatList(): Mono<List<Chat>>;
}