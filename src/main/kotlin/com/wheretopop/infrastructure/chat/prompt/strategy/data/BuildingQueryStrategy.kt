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
 * Building information query strategy implementation
 * Collects detailed building information including specifications, facilities, and approval details
 */
@Component
class BuildingQueryStrategy(
    private val areaToolRegistry: AreaToolRegistry,
    private val popupToolRegistry: PopupToolRegistry,
    private val buildingToolRegistry: BuildingToolRegistry,
    private val syncMcpToolCallbackProvider: SyncMcpToolCallbackProvider
) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}
    private val mcpToolCallbacks = syncMcpToolCallbackProvider.toolCallbacks

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.BUILDING_QUERY
    }

    /**
     * Returns building query specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Collect detailed building specifications and facility data.
            
            DATA COLLECTION PRIORITIES:
            - Primary focus: Building specifications, floor plans, and facility details
            - Location context: Area demographics and accessibility for building location
            - Historical data: Previous popup activities within the building
            
            TOOL REGISTRY USAGE:
            - **Building Tools (Primary)**: Physical specs, facilities, permits, accessibility
            - **Area Tools (Context)**: Location demographics and surrounding area analysis
            - **Popup Tools (History)**: Past popup events and performance in this building
            
            DATA GATHERING APPROACH:
            - Start with building identification and physical specifications
            - Gather facility details and regulatory compliance status
            - Cross-reference with location area characteristics
            - Include historical popup usage patterns if available
            
            RESPONSE GUIDANCE:
            - TARGET AUDIENCE: Subsequent ReAct analysis and recommendation strategies
            - PURPOSE: Deliver actionable building intelligence for location feasibility assessment
            - FORMAT: Structure data into facility specs, commercial viability, and operational constraints
            - TONE: Technical, specification-focused (like a building inspection report)
            - FOCUS: Measurable attributes, compliance status, practical limitations and opportunities
            
            Output structured building data that enables precise suitability calculations in next steps.
        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for building queries
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for building queries")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.1)
            .build()
            
        return toolCallbackChatOptions
    }
} 