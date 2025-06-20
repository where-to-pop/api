package com.wheretopop.infrastructure.chat.prompt.strategy.generation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * Location recommendation generation strategy implementation
 * Generates location and building recommendation responses with detailed rationale
 */
@Component
class LocationRecommendationResponseStrategy : BaseChatPromptStrategy() {

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.LOCATION_RECOMMENDATION_RESPONSE
    }

    /**
     * Returns location recommendation generation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Synthesize collected location retrieval into actionable recommendations.
            
            CONTEXT INTEGRATION:
            - Combine area analysis, building retrieval, and market insights from previous steps
            - Transform technical retrieval into business-focused recommendations
            - Prioritize options based on user's specific requirements and constraints
            - Connect retrieval points to create compelling business cases
            
            UX CONSIDERATIONS:
            - Lead with clear, confident recommendations before detailed analysis
            - Use storytelling to make retrieval relatable and memorable
            - Address augmentation anxiety with risk assessment and mitigation
            - Provide confidence levels to guide augmentation-making
            
            Synthesize all collected context into a compelling, user-friendly recommendation narrative.
        """.trimIndent()
    }

    /**
     * Location recommendation responses work with processed retrieval and don't require additional tool calls
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {

        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .internalToolExecutionEnabled(false)
            .temperature(0.3)
            .build()

        return toolCallbackChatOptions
    }
} 