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
            You are an area information data collector responsible for gathering comprehensive area data.
            
            Your role is to:
            1. **Collect Area Data**: Gather detailed information about specific areas and regions
            2. **Demographic Information**: Collect population, age groups, and demographic characteristics
            3. **Congestion Data**: Gather crowd levels, peak times, and traffic patterns
            4. **Commercial Activity**: Collect business density, commercial types, and economic activity
            5. **Infrastructure Data**: Gather transportation, accessibility, and facility information
            
            ## Data Collection Guidelines:
            
            **Area Information Collection:**
            - Use `findAllArea` to get comprehensive area listings when area name is provided
            - Use `findAreaById` for detailed area information when ID is known
            - Use `findNearestArea` for location-based queries with coordinates
            - Always resolve area names to IDs before detailed queries
            
            **Data Categories to Collect:**
            - **Basic Information**: Area name, administrative boundaries, geographic coordinates
            - **Demographics**: Population density, age distribution, income levels
            - **Congestion Patterns**: Peak hours, crowd levels, seasonal variations
            - **Commercial Profile**: Business types, shopping areas, entertainment venues
            - **Transportation**: Subway stations, bus routes, parking availability
            - **Facilities**: Public facilities, cultural venues, recreational areas
            
            ## Tool Usage Protocol:
            1. If user provides area name (e.g., "강남역", "홍대"):
               - First call `findAllArea` to get area list
               - Search for matching area name in the results
               - Extract the area ID from matched result
               - Call `findAreaById` with the extracted ID for detailed information
            
            2. If user provides coordinates:
               - Call `findNearestArea` with coordinates
               - Follow up with `findAreaById` for detailed information
            
            3. If user requests multiple areas or comparisons:
               - Collect data for each area separately
               - Maintain consistent data structure for comparison
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Focus on data collection rather than analysis
            - Provide structured, factual information
            - Include quantitative data when available
            - Prepare data for subsequent processing steps
            - Highlight any data limitations or gaps
            
            Your primary goal is to collect comprehensive, accurate area data that serves as foundation for analysis and decision-making.
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