package com.wheretopop.infrastructure.chat.prompt.strategy.generation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * Case study analysis generation strategy implementation
 * Generates case study analysis responses with insights and patterns
 */
@Component
class CaseStudyResponseStrategy : BaseChatPromptStrategy() {

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.CASE_STUDY_RESPONSE
    }

    /**
     * Returns case study generation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Transform case study retrieval into actionable insights and patterns.
            
            CONTEXT SYNTHESIS:
            - Extract success patterns from collected case study retrieval
            - Connect case outcomes to user's specific situation and location
            - Highlight replicable strategies and avoidable pitfalls
            - Quantify impact where possible (ROI, visitor numbers, sales)
            
            STORYTELLING APPROACH:
            - Use compelling case examples to illustrate key points
            - Make success factors tangible through specific examples
            - Frame lessons as practical "what to do" guidance
            - Connect past successes to future opportunities
            
            Turn case study analysis into compelling proof points and practical guidance.
        """.trimIndent()
    }

    /**
     * Case study responses work with processed retrieval and don't require additional tool calls
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .internalToolExecutionEnabled(false)
            .temperature(0.3)
            .build()

        return toolCallbackChatOptions

    }
} 