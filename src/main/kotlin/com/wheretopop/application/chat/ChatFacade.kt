package com.wheretopop.application.chat

import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatInfo
import com.wheretopop.domain.user.UserId

interface ChatFacade {
    /**
     * 새로운 채팅을 초기화합니다.
     */
    suspend fun initialize(input: ChatInput.Initialize): ChatInfo.Detail

    /**
     * 채팅 정보를 업데이트합니다.
     */
    suspend fun update(input: ChatInput.Update): ChatInfo.Main

    /**
     * 채팅을 삭제합니다.
     */
    suspend fun delete(input: ChatInput.Delete): ChatInfo.Main

    /**
     * 채팅 메시지를 전송합니다.
     */
    suspend fun sendMessage(input: ChatInput.SendMessage): ChatInfo.Simple

    /**
     * 채팅의 상세 정보를 조회합니다.
     */
    suspend fun getDetail(chatId: ChatId): ChatInfo.Detail

    /**
     * 사용자의 채팅 목록을 조회합니다.
     */
    suspend fun getList(userId: UserId): List<ChatInfo.Main>
}
