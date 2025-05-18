package com.wheretopop.domain.chat

interface GenerateChatTitle {
    /**
     * 채팅 제목을 생성합니다.
     *
     * @param chat 현재 채팅 상태
     * @return 생성된 채팅 제목
     */
    fun execute(userMessage: String): String
}


interface ProcessUserMessage {
    /**
     * 사용자 메시지를 처리하고 AI 응답을 포함한 업데이트된 채팅을 반환합니다.
     */
    fun execute(chat: Chat): Chat
}