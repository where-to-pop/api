package com.wheretopop.infrastructure.chat.prompt

import org.springframework.stereotype.Component

enum class PromptStrategyType {
    CONVERSATION,
    SUMMARIZE
}

@Component
class ChatPromptStrategyFactory(
    private val strategies: List<ChatPromptStrategy>
) {
    /**
     * 주어진 컨텍스트에 적합한 전략을 찾아 반환합니다.
     * 적합한 전략이 없으면 IllegalArgumentException을 발생시킵니다.
     */
    fun findStrategy(context: PromptContext): ChatPromptStrategy {
        return strategies.find { it.canHandle(context) }
            ?: throw IllegalArgumentException("No suitable strategy found for the given context")
    }

    /**
     * 특정 타입의 전략을 반환합니다.
     * 해당 타입의 전략이 컨텍스트를 처리할 수 없으면 IllegalArgumentException을 발생시킵니다.
     */
    fun getStrategy(type: PromptStrategyType, context: PromptContext): ChatPromptStrategy {
        val strategy = when (type) {
            PromptStrategyType.CONVERSATION -> strategies.filterIsInstance<ConversationPromptStrategy>()
            PromptStrategyType.SUMMARIZE -> strategies.filterIsInstance<SummarizePromptStrategy>()
        }.firstOrNull() ?: throw IllegalArgumentException("Strategy not found for type: $type")

        require(strategy.canHandle(context)) { 
            "Strategy ${type.name} cannot handle the given context. Required: ${strategy.getRequirements()}" 
        }

        return strategy
    }
} 