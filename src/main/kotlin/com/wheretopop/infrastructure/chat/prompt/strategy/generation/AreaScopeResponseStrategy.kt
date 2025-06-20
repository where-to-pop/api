package com.wheretopop.infrastructure.chat.prompt.strategy.generation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import mu.KotlinLogging
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * Area scope definition generation strategy implementation
 * Generates responses for area scope definition queries with geographical boundaries
 */
@Component
class AreaScopeResponseStrategy(

) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.AREA_SCOPE_RESPONSE
    }

    /**
     * Returns area scope generation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Define geographical area boundaries and scope for pop-up site selection.
            
            FOCUS AREAS:
            - Exact boundary definition with key landmarks
            - Commercial district characteristics and sub-zones
            - Transit accessibility and pedestrian flow patterns
            - Peak activity times and seasonal variations
            
            FORMAT:
            1. 경계 정의: Clear boundary description with landmarks
            2. 상권 특성: Business district characteristics  
            3. 접근성: Transit and foot traffic analysis
            4. 시간대별 특성: Peak times and seasonal patterns
            
            Emphasize practical boundary understanding for site selection decisions.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for area scope responses
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {

        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .temperature(0.4)
            .build()
            
        return toolCallbackChatOptions
    }
} 