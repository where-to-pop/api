package com.wheretopop.infrastructure.chat.prompt.strategy.data

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.interfaces.area.AreaToolRegistry
import com.wheretopop.interfaces.building.BuildingToolRegistry
import com.wheretopop.interfaces.popup.PopupToolRegistry
import io.modelcontextprotocol.client.McpSyncClient
import mu.KotlinLogging
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.support.ToolCallbacks
import org.springframework.stereotype.Component

/**
 * Data aggregation strategy implementation
 * Aggregates and combines information from multiple data sources
 */
@Component
class DataAggregationStrategy(
    private val areaToolRegistry: AreaToolRegistry,
    private val popupToolRegistry: PopupToolRegistry,
    private val buildingToolRegistry: BuildingToolRegistry,
    private val mcpSyncClients: List<McpSyncClient>
) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}
    private val syncMcpToolCallbackProvider = SyncMcpToolCallbackProvider(mcpSyncClients)
    private val mcpToolCallbacks = syncMcpToolCallbackProvider.toolCallbacks

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.DATA_AGGREGATION
    }

    /**
     * Returns data aggregation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Synthesize collected context and identify information gaps for user insights.
            
            CORE RESPONSIBILITIES:
            - Organize and integrate data from previous collection steps
            - Identify patterns and connections across area, building, and popup data
            - Assess information completeness against user's specific requirements
            - Fill critical gaps through targeted additional data collection
            
            CONTEXT ANALYSIS APPROACH:
            - Review all previously collected data for relevance and completeness
            - Map data relationships (area ↔ building ↔ popup success patterns)
            - Identify missing pieces crucial for user's decision-making
            - Prioritize gap-filling based on business impact
            
            SMART GAP IDENTIFICATION:
            - **Critical Gaps**: Missing data that blocks decision-making
            - **Insight Gaps**: Additional context that enhances recommendations
            - **Validation Gaps**: Information needed to confirm hypotheses
            - **Competitive Gaps**: Market intelligence for strategic positioning
            
            TOOL USAGE STRATEGY:
            - Use all tool registries selectively to fill identified gaps only
            - Focus on high-impact missing information rather than comprehensive collection
            - Cross-validate conflicting information through additional sources
            
            Transform scattered data into coherent insights while filling critical information gaps.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for data aggregation
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for data aggregation")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.3)
            .build()
            
        return toolCallbackChatOptions
    }
} 