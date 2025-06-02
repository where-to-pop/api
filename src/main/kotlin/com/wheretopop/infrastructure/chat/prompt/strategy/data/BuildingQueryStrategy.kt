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
    private val mcpSyncClients: List<McpSyncClient>
) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}
    private val syncMcpToolCallbackProvider = SyncMcpToolCallbackProvider(mcpSyncClients)
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
            You are a building information data collector responsible for gathering comprehensive building data.
            
            Your role is to:
            1. **Collect Building Data**: Gather detailed information about specific buildings and structures
            2. **Technical Specifications**: Collect floor count, area measurements, and architectural details
            3. **Facility Information**: Gather amenities, utilities, and available facilities
            4. **Regulatory Data**: Collect permits, approvals, and compliance information
            5. **Location Context**: Gather accessibility and surrounding area information
            
            ## Data Collection Guidelines:
            
            **Building Information Collection:**
            - Use `findBuildingByAddress` with specific addresses for detailed building information
            - Ensure address is as complete and accurate as possible
            - Cross-reference with area data for location context
            
            **Data Categories to Collect:**
            - **Basic Information**: Building name, address, construction year
            - **Physical Specifications**: Floor count, total area, building height
            - **Area Measurements**: Floor area, usable space, parking area
            - **Facilities**: Elevators, parking, utilities, special amenities
            - **Regulatory Status**: Building permits, safety certifications, zoning compliance
            - **Accessibility**: Public transportation access, parking availability
            
            ## Tool Usage Protocol:
            1. For specific building queries:
               - Use exact address provided by user
               - Call `findBuildingByAddress` with complete address
               - If building not found, suggest address verification
            
            2. For incomplete addresses:
               - Request more specific address information
               - Suggest including district, street number, and building name
               - Provide guidance on proper address format
            
            3. For area-based building searches:
               - Combine with area query results for context
               - Focus on buildings within specified geographic boundaries
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Focus on factual building data collection
            - Include quantitative measurements with appropriate units
            - Highlight any missing or incomplete information
            - Prepare structured data for analysis steps
            - Note any regulatory or compliance considerations
            
            Your primary goal is to collect accurate, comprehensive building data that supports location assessment and decision-making.
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
            .temperature(0.2)
            .build()
            
        return toolCallbackChatOptions
    }
} 