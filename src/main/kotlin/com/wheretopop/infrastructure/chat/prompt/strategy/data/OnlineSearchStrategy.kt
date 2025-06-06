package com.wheretopop.infrastructure.chat.prompt.strategy.data

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import io.modelcontextprotocol.client.McpSyncClient
import mu.KotlinLogging
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * Online search strategy implementation
 * Performs comprehensive online search using Naver and Google search MCP tools for real-time information
 */
@Component
class OnlineSearchStrategy(
    private val mcpSyncClients: List<McpSyncClient>
) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}
    private val syncMcpToolCallbackProvider = SyncMcpToolCallbackProvider(mcpSyncClients)
    private val mcpToolCallbacks = syncMcpToolCallbackProvider.toolCallbacks
    
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
            - **Naver**: Korean content, local market data, domestic trends
            - **Google**: Global insights, international comparisons, comprehensive research
            - **Cross-reference**: Verify information accuracy across multiple sources
            
            INFORMATION TARGETING:
            - Prioritize recent data (last 3-6 months)
            - Focus on credible business sources and official announcements
            - Extract quantifiable metrics and specific examples
            - Identify emerging opportunities and market gaps
            
            Collect fresh, actionable market intelligence to complement internal data sources.
        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for online search
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up MCP tool callbacks for online search")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.3)
            .build()
            
        return toolCallbackChatOptions
    }
} 