package com.wheretopop.interfaces.chat

import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.shared.exception.NotImplementedException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

/**
 * 채팅 요청 본문 데이터 클래스
 */
data class ChatRequest(
    val prompt: String,
    val systemPrompt: String? = null
)

@Configuration
class ChatApiRouter(private val chatHandler: ChatHandler) {
    @Bean
    fun chatRoutes(): RouterFunction<ServerResponse> {
        return coRouter {
            "/v1/chats".nest {
                GET("/{id}", chatHandler::getChat)
                GET("", chatHandler::getChatList)
            }
        }
    }
}

@Component
class ChatHandler(
    private val chatFacade: ChatFacade,
) {
    suspend fun getChat(request: ServerRequest): ServerResponse {
        throw NotImplementedException()
    }
    
    suspend fun getChatList(request: ServerRequest): ServerResponse {
        throw NotImplementedException()
    }
}