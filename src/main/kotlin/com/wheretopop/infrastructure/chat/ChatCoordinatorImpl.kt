package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatCoordinator
import com.wheretopop.domain.chat.ChatMessage
import com.wheretopop.shared.enums.ChatMessageFinishReason
import com.wheretopop.shared.enums.ChatMessageRole
import org.springframework.stereotype.Service
import java.time.Instant


/**
 * ChatAssistant 와 ChatPromptBuilder 를 조율하여 대화를 처리하는 코디네이터 클래스입니다.
 * 이 클래스는 채팅 관련 도메인 로직을 담당하며, 서비스 레이어와 AI 컴포넌트 사이의 중간 계층 역할을 합니다.
 */
@Service
class ChatCoordinatorImpl(
    private val chatAssistant: ChatAssistant,
    private val chatPromptBuilder: ChatPromptBuilder
): ChatCoordinator {
    /**
     * 사용자 메시지를 받아 AI 응답을 처리하고 Chat 객체를 업데이트합니다.
     *
     * @param chat 현재 채팅 객체
     * @param userContent 사용자 메시지 내용
     * @return 업데이트된 Chat 객체 (새 메시지 포함)
     */
    override suspend fun processUserMessage(chat: Chat, userContent: String): Chat {
        // 1. 사용자 메시지 생성
        val userMessage = ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.USER,
            content = userContent,
            finishReason = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        )

        // 2. 사용자 메시지를 채팅에 추가
        val updatedChat = chat.addMessage(userMessage)

        // 3. 프롬프트 생성 및 AI 응답 요청 (직접 content 얻기)
        val startTime = System.currentTimeMillis()
        val prompt = chatPromptBuilder.buildPrompt(updatedChat, userMessage)
        val aiContent = chatAssistant.call(prompt) // 단순화: 직접 content 반환
        val endTime = System.currentTimeMillis()

        // 4. AI 응답 메시지 생성
        val assistantMessage = ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.ASSISTANT,
            content = aiContent,
            finishReason = ChatMessageFinishReason.STOP, // 기본값으로 정상 종료 처리
            latencyMs = endTime - startTime,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        )

        // 5. AI 응답 메시지를 채팅에 추가하여 반환
        return updatedChat.addMessage(assistantMessage)
    }

    /**
     * 시스템 프롬프트를 설정하고 사용자 메시지를 처리합니다.
     *
     * @param chat 현재 채팅 객체
     * @param systemPrompt 시스템 프롬프트 내용
     * @param userContent 사용자 메시지 내용
     * @return 업데이트된 Chat 객체
     */
    override suspend fun processWithSystemPrompt(chat: Chat, systemPrompt: String, userContent: String): Chat {
        // 1. 시스템 메시지 생성
        val systemMessage = ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.SYSTEM,
            content = systemPrompt,
            finishReason = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        )

        // 2. 시스템 메시지를 채팅에 추가
        val chatWithSystem = chat.addMessage(systemMessage)

        // 3. 사용자 메시지 처리
        return processUserMessage(chatWithSystem, userContent)
    }
}