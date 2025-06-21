package com.wheretopop.infrastructure.chat.prompt.strategy.retrieval

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.tool.ToolCallback
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Trend information query strategy implementation
 * Collects industry trends, consumer behavior patterns, and market insights using web search
 */
@Component
class TrendQueryStrategy(
    @Qualifier("searchToolCallbacks")
    private val mcpToolCallbacks: Array<ToolCallback>
) : BaseChatPromptStrategy() {

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.TREND_QUERY
    }

    /**
     * Returns trend query specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Collect comprehensive industry trends and consumer behavior insights for popup store market intelligence.
            
            TREND RESEARCH PRIORITIES:
            - Latest popup store and retail industry trends (2024-2025)
            - Consumer behavior changes and preferences
            - Emerging business models and innovative concepts
            - Technology integration in retail experiences
            - Seasonal and demographic trend patterns
            
            SEARCH FOCUS AREAS:
            1. **업계 동향**: Popup store industry reports, market forecasts, growth statistics
            2. **소비자 트렌드**: Shopping behavior studies, generational preferences, lifestyle changes
            3. **혁신 사례**: New popup concepts, technology adoption, creative marketing approaches
            4. **시장 변화**: Economic factors affecting retail, post-pandemic behaviors, digital integration
            
            SEARCH ENGINE STRATEGY:
            - **Naver**: Korean market trends, domestic consumer insights, local industry reports
            - **Google**: Global retail trends, international best practices, research studies
            - **Cross-validation**: Compare Korean vs global trends, identify applicable insights
            
            INFORMATION QUALITY CRITERIA:
            - Prioritize data from last 6-12 months for relevancy
            - Focus on credible sources: industry reports, market research, business journals
            - Extract quantifiable metrics and statistical evidence
            - Identify recurring patterns across multiple sources
            
            
            RESPONSE GUIDANCE:
            You don't need to generate any response. Just define the functions you need to call.
            If you think that no more functions are needed, just return empty string.
        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for trend query
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.1)
            .build()
            
        return toolCallbackChatOptions
    }
} 