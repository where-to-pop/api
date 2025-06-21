package com.wheretopop.infrastructure.chat.prompt.strategy.retrieval

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.tool.ToolCallback
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Online search strategy implementation
 * Performs comprehensive online search using Naver and Google search MCP tools for real-time information
 */
@Component
class OnlineSearchStrategy(
    @Qualifier("searchToolCallbacks")
    private val mcpToolCallbacks: Array<ToolCallback>
) : BaseChatPromptStrategy() {


    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.ONLINE_SEARCH
    }

    /**
     * Returns online search specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Gather real-time market intelligence and current trends from web sources.
            
            SEARCH PRIORITIES:
            - Current market trends and industry developments
            - Recent popup success stories and case studies
            - Competitive landscape and pricing intelligence
            - Local market conditions and development news
            
            SEARCH ENGINE STRATEGY:
            - **Naver**: Korean content, local market retrieval, domestic trends
            - **Google**: Global insights, international comparisons, comprehensive research
            - **Cross-reference**: Verify information accuracy across multiple sources
            
            INFORMATION TARGETING:
            - Prioritize recent retrieval (last 3-6 months)
            - Focus on credible business sources and official announcements
            - Extract quantifiable metrics and specific examples
            - Identify emerging opportunities and market gaps
            
            RESPONSE GUIDANCE:
            You don't need to generate any response. Just return "정보 조회를 완료했습니다".
        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for online search
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