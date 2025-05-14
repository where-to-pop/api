package com.wheretopop.domain.chat

/**
 * 채팅 처리를 위한 코디네이터 인터페이스
 * 이 인터페이스는 채팅과 AI 어시스턴트 사이의 중재자 역할을 합니다.
 */
interface ChatCoordinator {
    /**
     * 사용자 메시지를 받아 처리하고 AI 응답을 포함한 업데이트된 채팅을 반환합니다.
     *
     * @param chat 현재 채팅 상태
     * @param userContent 사용자 메시지 내용
     * @return 업데이트된 채팅 (AI 응답 포함)
     */
    suspend fun processUserMessage(chat: Chat, userContent: String): Chat

    /**
     * 시스템 프롬프트를 기반으로 요약 제목을 생성합니다.
     *
     * @param chat 현재 채팅 상태
     * @return 요약 제목
     */
    suspend fun summarizeTitle(chat: Chat): String

    /**
     * 시스템 프롬프트를 설정하고 사용자 메시지를 처리합니다.
     *
     * @param chat 현재 채팅 상태
     * @param systemPrompt 시스템 프롬프트 내용
     * @param userContent 사용자 메시지 내용
     * @return 업데이트된 채팅 (시스템 메시지 및 AI 응답 포함)
     */
    suspend fun processWithSystemPrompt(chat: Chat, systemPrompt: String, userContent: String): Chat
}