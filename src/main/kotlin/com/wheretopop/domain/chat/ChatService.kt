package com.wheretopop.domain.chat

import com.wheretopop.domain.user.UserId
import kotlinx.coroutines.flow.Flow

interface ChatService {
    fun initializeChat(command: ChatCommand.InitializeChat): ChatInfo.Detail
    fun updateChat(command: ChatCommand.UpdateChat): ChatInfo.Main
    fun deleteChat(command: ChatCommand.DeleteChat): ChatInfo.Main
    
    /**
     * 채팅 메시지를 전송합니다 (비동기 실행 시작, 즉시 Simple 정보 반환)
     * ReAct 실행은 백그라운드에서 진행되며, 실행 상태는 getChatExecutionStatusStream으로 모니터링 가능합니다.
     */
    fun sendMessage(chatId: ChatId, message: String, context: String?): ChatInfo.Simple
    
    /**
     * 특정 채팅의 ReAct 실행 상태를 스트림으로 조회합니다.
     */
    fun getChatExecutionStatusStream(chatId: ChatId, userId: UserId, executionId: String?): Flow<String>

    fun getSimple(chatId: ChatId): ChatInfo.Simple
    fun getDetail(chatId: ChatId): ChatInfo.Detail
    fun getList(userId: UserId): List<ChatInfo.Main>
}