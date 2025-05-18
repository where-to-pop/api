package com.wheretopop.interfaces.chat

import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.application.chat.ChatInput
import com.wheretopop.config.security.CurrentUser
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.response.CommonResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

/**
 * 채팅(Chat) 관련 컨트롤러
 * Spring MVC 기반으로 구현
 */
@RestController
@RequestMapping("/v1/chats")
class ChatController(private val chatFacade: ChatFacade) {
    
    /**
     * 새로운 채팅을 초기화합니다.
     */
    @PostMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun initializeChat(
        @RequestBody initializeRequest: ChatDto.InitializeRequest,
        @CurrentUser userId: UserId
    ): CommonResponse<ChatDto.ChatDetailResponse> {
        val chatInfo = chatFacade.initialize(
            ChatInput.Initialize(
                userId = userId,
                projectId = ProjectId.of(initializeRequest.projectId),
                initialMessage = initializeRequest.initialMessage
            )
        )
        
        return CommonResponse.success(ChatDto.ChatDetailResponse.from(chatInfo))
    }
    
    /**
     * 채팅 정보를 수정합니다.
     */
    @PutMapping("/{chatId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateChat(
        @PathVariable chatId: Long,
        @RequestBody updateRequest: ChatDto.UpdateRequest,
        @CurrentUser userId: UserId
    ): CommonResponse<ChatDto.ChatResponse> {
        val chatInfo = chatFacade.update(
            ChatInput.Update(
                chatId = ChatId.of(chatId),
                title = updateRequest.title
            )
        )
        
        return CommonResponse.success(ChatDto.ChatResponse.from(chatInfo))
    }
    
    /**
     * 채팅 상세 정보를 조회합니다.
     */
    @GetMapping("/{chatId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChat(
        @PathVariable chatId: Long,
        @CurrentUser userId: UserId
    ): CommonResponse<ChatDto.ChatDetailResponse> {
        val chatInfo = chatFacade.getDetail(ChatId.of(chatId))
        
        return CommonResponse.success(ChatDto.ChatDetailResponse.from(chatInfo))
    }
    
    /**
     * 사용자의 채팅 목록을 조회합니다.
     */
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChatList(
        @CurrentUser userId: UserId
    ): CommonResponse<List<ChatDto.ChatResponse>> {
        val chatInfos = chatFacade.getList(userId)
        
        return CommonResponse.success(
            chatInfos.map { ChatDto.ChatResponse.from(it) }
        )
    }
    
    /**
     * 채팅 메시지를 전송합니다.
     */
    @PostMapping("/{chatId}/messages", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun sendMessage(
        @PathVariable chatId: Long,
        @RequestBody messageRequest: ChatDto.SendMessageRequest,
        @CurrentUser userId: UserId
    ): CommonResponse<ChatDto.ChatSimpleResponse> {
        val chatInfo = chatFacade.sendMessage(
            ChatInput.SendMessage(
                chatId = ChatId.of(chatId),
                userId = userId,
                message = messageRequest.message
            )
        )
        
        return CommonResponse.success(ChatDto.ChatSimpleResponse.from(chatInfo))
    }
}
