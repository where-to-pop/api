package com.wheretopop.infrastructure.chat.prompt

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * 사용자 메시지를 분석하여 적절한 전략을 선택하는 전략 구현체
 */
@Component
class StrategySelectorStrategy : BaseChatPromptStrategy() {

    /**
     * 전략 타입을 반환합니다.
     */
    override fun getType(): StrategyType {
        return StrategyType.STRATEGY_SELECTOR
    }

    /**
     * 전략 선택에 특화된 시스템 프롬프트를 생성합니다.
     * 사용자 메시지를 분석하여 가장 적합한 전략을 선택하도록 안내합니다.
     */
    override fun getSystemPrompt(): String {
        // 사용 가능한 모든 전략들의 정보를 프롬프트에 포함
        val strategies = StrategyType.values()
            .filter { it != StrategyType.STRATEGY_SELECTOR } // 자기 자신은 제외
            .filter { it != StrategyType.TITLE_GENERATION } // 타이틀 생성 제외
            .joinToString("\n") { strategy ->
                "- ${strategy.id}: ${strategy.description}"
            }
        
        return """
            You are a strategy selector for the WhereToPop chatbot. Your task is to analyze the user's message and determine the most appropriate strategy to use.
            
            Available strategies:
            $strategies
            
            Guidelines:
            1. Choose the most appropriate strategy based on the user's message content
            2. If the message is about specific areas, locations, or congestion, choose 'area_query'
            3. For the very first message in a conversation when a title needs to be generated, choose 'title_generation'
            4. Respond ONLY with the strategy ID, nothing else
        """.trimIndent()
    }
    
    /**
     * 전략 선택을 위한 프롬프트 생성
     * 일반적인 추가 시스템 프롬프트 대신 선택을 위한 특수 프롬프트를 만듭니다.
     */
    override fun createPrompt(userMessage: String): Prompt {
        val messages: MutableList<Message> = mutableListOf()
        
        // 기본 시스템 프롬프트 추가 (전략 선택을 위한 프롬프트)
        messages.add(SystemMessage(getSystemPrompt()))
        
        // 사용자 메시지를 분석 대상으로 추가
        messages.add(UserMessage("Analyze this message and choose the most appropriate strategy: \"$userMessage\""))
        
        return Prompt(messages)
    }
    
    /**
     * 전략 선택은 단순한 작업이므로 Tool Calling을 사용하지 않습니다.
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {
        return null
    }
} 