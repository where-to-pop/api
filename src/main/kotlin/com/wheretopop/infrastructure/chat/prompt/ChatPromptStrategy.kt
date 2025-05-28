package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions

/**
 * 챗봇 프롬프트 전략 인터페이스
 * 사용자 메시지에 대한 적절한 프롬프트 생성과 호출 방식을 결정하는 전략을 정의합니다.
 */
interface ChatPromptStrategy {
    
    /**
     * 전략의 타입을 반환합니다.
     * 각 전략 구현체는 고유한 타입을 가져야 합니다.
     */
    fun getType(): StrategyType
    
    /**
     * 사용자 메시지를 처리하고 챗봇에 전달할 프롬프트를 생성합니다.
     * 
     * @param userMessage 사용자가 전송한 메시지
     * @return 챗봇에 전달할 프롬프트 객체
     */
    fun createPrompt(userMessage: String): Prompt
    

    /**
     * Tool Calling을 사용하는 경우, 적절한 ToolCallingChatOptions를 제공합니다.
     * Tool Registry 설정 등이 여기서 이루어집니다.
     * 
     * @return Tool Calling 채팅 옵션 객체
     */
    fun getToolCallingChatOptions(): ToolCallingChatOptions?
} 