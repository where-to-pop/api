package com.wheretopop.application.chat

import com.wheretopop.domain.area.Area
import reactor.core.publisher.Mono
import javax.swing.Popup

data class ChatContext(
    val userMessage: String,
//    val recentMessages: List<ChatMessage>,
    val area: Area?,
//    val building: Building?,
    val popup: Popup?,
    val prompt: String // 완성된 프롬프트
)

// NOTE: 이 data class를 통해 프롬프트 탬플릿을 엔지니어링 합니다.
data class ChatPrompt (
    val context: String,
    val message: String
)

interface ChatPromptBuilder {
    fun createContext(chatId: Long, message: String): Mono<ChatContext>
    fun buildPrompt(context: ChatContext): ChatPrompt
}
