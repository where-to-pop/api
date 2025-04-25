package com.wheretopop.interfaces.chat

import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.shared.exception.NotImplementedException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Configuration
class ChatApiRouter(private val chatHandler: ChatHandler) {

    @Bean
    fun chatRoutes() = router {
        "/v1/chats".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", chatHandler::getChatList)
                GET("/:chatId", chatHandler::getChat)
            }
            accept(MediaType.TEXT_EVENT_STREAM).nest {
                GET("/:chatId/streams", chatHandler::getChatStream)
            }
        }
    }
}

@Component
class ChatHandler(private val chatFacade: ChatFacade) {
    fun getChat(request: ServerRequest): Mono<ServerResponse> {
        throw NotImplementedException()
    }
    fun getChatList(request: ServerRequest): Mono<ServerResponse> {
        throw NotImplementedException()
    }

    fun getChatStream(request: ServerRequest): Mono<ServerResponse> {
        val eventStream: Flux<ServerSentEvent<String>> =
            Flux.interval(Duration.ofSeconds(1))
                .map { tick ->
                    ServerSentEvent.builder("메시지 #$tick")
                        .id(tick.toString())
                        .event("chat-event")
                        .build()
                }

        return ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(eventStream, ServerSentEvent::class.java)
    }
}