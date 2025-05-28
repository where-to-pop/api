package com.wheretopop.infrastructure.chat.prompt.strategy

import com.wheretopop.infrastructure.chat.prompt.ChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.SystemPrompt
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions

/**
 * 챗봇 프롬프트 전략의 기본 추상 클래스.
 * 공통 기능을 구현하고 특정 전략에서 오버라이드할 함수를 정의합니다.
 */
abstract class BaseChatPromptStrategy : ChatPromptStrategy {
    
    /**
     * 기본 시스템 프롬프트를 반환합니다.
     * 모든 전략에서 공통으로 사용되는 프롬프트를 정의합니다.
     * 
     * @return 시스템 프롬프트 문자열
     */
    protected open fun getSystemPrompt(): String {
        return SystemPrompt.BASE_PROMPT
    }
    
    /**
     * 추가적인 시스템 프롬프트를 반환합니다.
     * 각 전략에 특화된 프롬프트를 구체 클래스에서 정의합니다.
     * 
     * @return 추가 시스템 프롬프트 문자열
     */
    protected open fun getAdditionalSystemPrompt(): String? {
        return null
    }
    
    /**
     * 사용자 메시지를 처리하고 챗봇에 전달할 프롬프트를 생성합니다.
     * 
     * @param userMessage 사용자가 전송한 메시지
     * @return 시스템 메시지와 사용자 메시지를 포함한 프롬프트 객체
     */
    override fun createPrompt(userMessage: String): Prompt {
        val messages: MutableList<Message> = mutableListOf()
        
        // 기본 시스템 프롬프트 추가
        messages.add(SystemMessage(getSystemPrompt()))
        
        // 추가 시스템 프롬프트가 있으면 추가
        getAdditionalSystemPrompt()?.let {
            messages.add(SystemMessage(it))
        }
        
        // 사용자 메시지 추가
        messages.add(UserMessage(userMessage))
        
        return Prompt(messages)
    }
    
    /**
     * 기본적으로는 Tool Calling을 사용하지 않습니다.
     * Tool Calling을 사용하는 전략에서 오버라이드해야 합니다.
     * 
     * @return null (Tool Calling 미사용)
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {
        return null
    }
} 