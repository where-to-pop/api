package com.wheretopop.interfaces.chat

import com.wheretopop.application.chat.ChatFacade
import com.wheretopop.application.chat.ChatInput
import com.wheretopop.config.security.CurrentUser
import com.wheretopop.config.security.UserPrincipal
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.util.SseUtil
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/**
 * 채팅(Chat) 관련 컨트롤러
 * Spring MVC 기반으로 구현
 */
@RestController
@RequestMapping("/v1/chats")
class ChatController(
    private val chatFacade: ChatFacade,
    private val sseUtil: SseUtil
) {
    val logger = KotlinLogging.logger {}
    
    /**
     * 새로운 채팅을 초기화합니다.
     */
    @PostMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun initializeChat(
        @RequestBody initializeRequest: ChatDto.InitializeRequest,
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<ChatDto.ChatDetailResponse> {
        val chatInfo = chatFacade.initialize(
            ChatInput.Initialize(
                userId = principal.userId,
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
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<ChatDto.ChatResponse> {
        val chatInfo = chatFacade.update(
            ChatInput.Update(
                chatId = ChatId.of(chatId),
                userId = principal.userId,
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
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<ChatDto.ChatDetailResponse> {
        val chatInfo = chatFacade.getDetail(ChatId.of(chatId))
        
        return CommonResponse.success(ChatDto.ChatDetailResponse.from(chatInfo))
    }
    
    /**
     * 사용자의 채팅 목록을 조회합니다.
     */
    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChatList(
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<List<ChatDto.ChatResponse>> {
        val chatInfos = chatFacade.getList(principal.userId)
        
        return CommonResponse.success(
            chatInfos.map { ChatDto.ChatResponse.from(it) }
        )
    }
    
    /**
     * 채팅 메시지를 전송합니다.
     * 메시지 전송 후 즉시 Simple 응답을 반환합니다.
     * 실시간 실행 상태는 GET /stream 엔드포인트를 통해 별도로 구독할 수 있습니다.
     */
    @PostMapping("/{chatId}/messages", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun sendMessage(
        @PathVariable chatId: Long,
        @RequestBody messageRequest: ChatDto.SendMessageRequest,
        @CurrentUser principal: UserPrincipal
    ): CommonResponse<ChatDto.ChatSimpleResponse> {
        val chatInfo = chatFacade.sendMessage(
            ChatInput.SendMessage(
                chatId = ChatId.of(chatId),
                userId = principal.userId,
                message = messageRequest.message
            )
        )
        
        return CommonResponse.success(ChatDto.ChatSimpleResponse.from(chatInfo))
    }

    /**
     * 특정 채팅의 ReAct 실행 상태를 스트림으로 조회합니다.
     * 
     * Server-Sent Events(SSE)를 통해 실시간으로 ReAct 다단계 실행 과정을 모니터링할 수 있습니다.
     * 메시지 전송은 POST /messages로 하고, 실행 상태는 이 엔드포인트로 별도 구독합니다.
     * 
     * @param chatId 채팅 ID
     * @param executionId 실행 ID (선택적)
     * @param principal 현재 사용자
     * @return ReAct 실행 상태 스트림
     */
    @GetMapping("/{chatId}/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChatExecutionStatusStream(
        @PathVariable chatId: Long,
        @RequestParam(required = false) executionId: String?,
        @CurrentUser principal: UserPrincipal
    ): SseEmitter {
        val statusFlow: Flow<String> = chatFacade.getChatExecutionStatusStream(
            ChatId.of(chatId),
            principal.userId,
            executionId
        )
        
        // Flow를 SseEmitter로 변환 (Spring MVC 안전 처리)
        return sseUtil.fromTextFlow(statusFlow)
    }
}
