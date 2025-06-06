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
 * Area information query strategy implementation
 * Collects comprehensive area information including congestion, demographics, and characteristics
 */
@Component
class AreaQueryStrategy(
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
        return StrategyType.AREA_QUERY
    }

    /**
     * Returns area query specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Collect comprehensive area data for location analysis.
            
            DATA COLLECTION PRIORITIES:
            - Primary focus: Area demographics, boundaries, and traffic patterns
            - Secondary context: Related buildings and facilities within the area
            - Supporting data: Existing popup activities and success cases in the area
            
            TOOL REGISTRY USAGE:
            - **Area Tools (Primary)**: Demographic data, congestion patterns, geographic boundaries
            - **Building Tools (Supporting)**: Commercial buildings and facilities within areas
            - **Popup Tools (Context)**: Historical popup activities and performance in areas
            
            DATA GATHERING APPROACH:
            - Start with area identification and boundary definition
            - Expand to demographic and traffic analysis
            - Cross-reference with building availability and popup history
            - Maintain focus on quantitative, factual data collection
            
            Collect raw data systematically without analysis - prepare foundation for next steps.
        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for area queries
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for area queries")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.2)
            .build()
            
        return toolCallbackChatOptions
    }
} 