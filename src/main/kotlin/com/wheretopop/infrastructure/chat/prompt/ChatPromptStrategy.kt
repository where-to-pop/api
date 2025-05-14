package com.wheretopop.infrastructure.chat.prompt

import org.springframework.ai.chat.prompt.Prompt

/**
 * 채팅 프롬프트 생성 전략 인터페이스
 */
interface ChatPromptStrategy {
    /**
     * 주어진 컨텍스트로 프롬프트를 생성할 수 있는지 확인합니다.
     */
    fun canHandle(context: PromptContext): Boolean

    /**
     * 주어진 컨텍스트로 프롬프트를 생성합니다.
     */
    suspend fun buildPrompt(context: PromptContext): Prompt

    /**
     * 전략에 필요한 컨텍스트 요구사항을 설명합니다.
     */
    fun getRequirements(): Set<PromptContextRequirement>
}

/**
 * 프롬프트 전략이 요구하는 컨텍스트 요소들
 */
enum class PromptContextRequirement {
    CHAT,               // 채팅 기록 필수
    USER_MESSAGE,       // 사용자 메시지 필수
    SYSTEM_MESSAGE,     // 시스템 메시지 필수
    ALLOW_EMPTY_CHAT,   // 빈 채팅 허용
    NO_USER_MESSAGE,    // 사용자 메시지 없어야 함
    NO_SYSTEM_MESSAGE   // 시스템 메시지 없어야 함
} 