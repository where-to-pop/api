package com.wheretopop.interfaces.chat

import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.shared.exception.NotImplementedException
import com.wheretopop.shared.util.SseUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

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
                accept(MediaType.APPLICATION_JSON).nest {
                    POST("", chatHandler::chat)
                }
                accept(MediaType.TEXT_EVENT_STREAM).nest {
                    GET("/{chatId}/streams", chatHandler::getChatStream)
                }
            }
        }
    }
}

@Component
class ChatHandler(
    private val chatFacade: ChatFacade,
    private val sseUtil: SseUtil
) {
    suspend fun getChat(request: ServerRequest): ServerResponse {
        throw NotImplementedException()
    }
    
    suspend fun getChatList(request: ServerRequest): ServerResponse {
        throw NotImplementedException()
    }
    
    // 채팅 요청 처리
    suspend fun chat(request: ServerRequest): ServerResponse {
        // 요청 본문에서 ChatRequest 객체로 변환
        val chatRequest = request.awaitBodyOrNull<ChatRequest>() ?: 
            ChatRequest(prompt = "기본 프롬프트")
        
        // ChatFacade 호출하여 응답 생성
        val chatResponse = this.chatFacade.chat(chatRequest.prompt)
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(chatResponse)
    }
    
    // 스트림 요청 처리
    suspend fun getChatStream(request: ServerRequest): ServerResponse {
        val chatId = request.pathVariable("chatId")
        
        // 쿼리 파라미터에서 prompt 가져오기
        val userPrompt = request.queryParamOrNull("prompt") ?: "현재시간을 알려줘 툴 활용해!"

        val generationFlow = chatFacade.streamGenerations(userPrompt)
        val sseFlow = sseUtil.fromGenerationFlow(generationFlow)

        return ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .bodyAndAwait(sseFlow)
    }
}