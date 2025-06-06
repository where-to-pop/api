package com.wheretopop.infrastructure.chat.prompt.strategy.response

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import mu.KotlinLogging
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * General conversation response strategy implementation
 * Generates general conversation responses for basic queries and interactions - used as fallback
 */
@Component
class GeneralResponseStrategy(
) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.GENERAL_RESPONSE
    }

    /**
     * Returns general response specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Handle general inquiries and guide users toward specific analysis needs.
            
            CONVERSATION PRIORITIES:
            - Understand user's underlying business goals beyond surface questions
            - Bridge general questions to specific location consulting opportunities
            - Maintain engagement while steering toward actionable analysis
            - Demonstrate value through quick, relevant insights
            
            GUIDANCE APPROACH:
            - For vague requests: Ask qualifying questions to identify specific needs
            - For greetings: Warm introduction with immediate value proposition
            - For confusion: Clarify service capabilities with concrete examples
            - For simple questions: Answer directly then suggest deeper analysis
            
            Turn casual conversations into meaningful location consulting opportunities.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for general responses
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {

        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .internalToolExecutionEnabled(false)
            .temperature(0.5) // Higher temperature for more natural conversation
            .build()
            
        return toolCallbackChatOptions
    }
} 