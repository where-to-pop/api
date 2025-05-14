package com.wheretopop.interfaces.chat

import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.application.chat.ChatInput
import com.wheretopop.config.security.AUTH_GET
import com.wheretopop.config.security.AUTH_POST
import com.wheretopop.config.security.AUTH_PUT
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.response.CommonResponse
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

/**
 * 채팅(Chat) 관련 라우터 정의
 * Spring WebFlux 함수형 엔드포인트 사용
 */
@Configuration
class ChatApiRouter(private val chatHandler: ChatHandler): RouterFunction<ServerResponse> {
    
    private val delegate = coRouter {
        "/v1/chats".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                // 새로운 채팅 초기화 (인증 필요)
                AUTH_POST("") { request, userId ->
                    chatHandler.initializeChat(request, userId)
                }
                
                // 채팅 정보 수정 (인증 필요)
                AUTH_PUT("/{chatId}") { request, userId ->
                    chatHandler.updateChat(request, userId)
                }
                
                // 채팅 상세 조회 (인증 필요)
                AUTH_GET("/{chatId}") { request, userId ->
                    chatHandler.getChat(request, userId)
                }
                
                // 사용자의 채팅 목록 조회 (인증 필요)
                AUTH_GET("") { request, userId ->
                    chatHandler.getChatList(request, userId)
                }
                
                // 채팅 메시지 전송 (인증 필요)
                AUTH_POST("/{chatId}/messages") { request, userId ->
                    chatHandler.sendMessage(request, userId)
                }
            }
        }
    }
    
    override fun route(request: ServerRequest): Mono<HandlerFunction<ServerResponse>> = delegate.route(request)
}

/**
 * 채팅(Chat) 관련 요청 처리 핸들러
 */
@Component
class ChatHandler(private val chatFacade: ChatFacade) {
    
    /**
     * 새로운 채팅을 초기화합니다.
     */
    suspend fun initializeChat(request: ServerRequest, userId: UserId): ServerResponse {
        val initializeRequest = request.awaitBody<ChatDto.InitializeRequest>()
        val chatInfo = chatFacade.initialize(
            ChatInput.Initialize(
            userId = userId,
            projectId = ProjectId.of(initializeRequest.projectId),
            initialMessage = initializeRequest.initialMessage
        ))
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(ChatDto.ChatDetailResponse.from(chatInfo)))
    }
    
    /**
     * 채팅 정보를 수정합니다.
     */
    suspend fun updateChat(request: ServerRequest, userId: UserId): ServerResponse {
        val chatId = ChatId.of(request.pathVariable("chatId").toLong())
        val updateRequest = request.awaitBody<ChatDto.UpdateRequest>()
        val chatInfo = chatFacade.update(
            ChatInput.Update(
            chatId = chatId,
            title = updateRequest.title
        ))
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(ChatDto.ChatResponse.from(chatInfo)))
    }
    
    /**
     * 채팅 상세 정보를 조회합니다.
     */
    suspend fun getChat(request: ServerRequest, userId: UserId): ServerResponse {
        val chatId = ChatId.of(request.pathVariable("chatId").toLong())
        val chatInfo = chatFacade.getDetail(chatId)
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(ChatDto.ChatDetailResponse.from(chatInfo)))
    }
    
    /**
     * 사용자의 채팅 목록을 조회합니다.
     */
    suspend fun getChatList(request: ServerRequest, userId: UserId): ServerResponse {
        val chatInfos = chatFacade.getList(userId)
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(
                chatInfos.map { ChatDto.ChatResponse.from(it) }
            ))
    }
    
    /**
     * 채팅 메시지를 전송합니다.
     */
    suspend fun sendMessage(request: ServerRequest, userId: UserId): ServerResponse {
        val chatId = ChatId.of(request.pathVariable("chatId").toLong())
        val messageRequest = request.awaitBody<ChatDto.SendMessageRequest>()
        val chatInfo = chatFacade.sendMessage(
            ChatInput.SendMessage(
            chatId = chatId,
            userId = userId,
            message = messageRequest.message
        ))
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueWithTypeAndAwait<CommonResponse<ChatDto.ChatSimpleResponse>>(CommonResponse.success(ChatDto.ChatSimpleResponse.from(chatInfo)))
    }
}
