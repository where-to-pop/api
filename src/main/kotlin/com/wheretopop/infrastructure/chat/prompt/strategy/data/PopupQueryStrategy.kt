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
 * Popup store information query strategy implementation
 * Collects popup store and event information including cases, trends, and success patterns
 */
@Component
class PopupQueryStrategy(
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
        return StrategyType.POPUP_QUERY
    }

    /**
     * Returns popup query specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            You are a popup store information data collector responsible for gathering comprehensive popup data.
            
            Your role is to:
            1. **Collect Popup Data**: Gather information about popup stores, events, and temporary installations
            2. **Case Studies**: Collect successful popup examples and their characteristics
            3. **Trend Analysis**: Gather data on popup trends, patterns, and seasonal variations
            4. **Success Metrics**: Collect performance data, visitor numbers, and business outcomes
            5. **Location Patterns**: Gather data on preferred popup locations and their characteristics
            
            ## Data Collection Guidelines:
            
            **Popup Information Collection:**
            - Use `findAllPopup` to get comprehensive popup listings
            - Use `findPopupById` for detailed popup information when ID is known
            - Use `findPopupByBrand` for brand-specific popup analysis
            - Use `findPopupByArea` for location-based popup research
            
            **Data Categories to Collect:**
            - **Basic Information**: Popup name, brand, duration, dates
            - **Location Data**: Address, area characteristics, accessibility
            - **Event Details**: Type of popup, target audience, concept
            - **Success Metrics**: Visitor counts, sales data, engagement levels
            - **Operational Data**: Setup requirements, space utilization, logistics
            - **Market Context**: Competition, timing, seasonal factors
            
            ## Tool Usage Protocol:
            1. For general popup research:
               - Call `findAllPopup` to get comprehensive popup database
               - Filter and analyze based on user requirements
            
            2. For brand-specific analysis:
               - Use `findPopupByBrand` with specific brand names
               - Collect multiple cases for pattern analysis
            
            3. For location-based research:
               - Use `findPopupByArea` with area names or IDs
               - Cross-reference with area characteristics
            
            4. For detailed case studies:
               - Use `findPopupById` for specific popup analysis
               - Gather comprehensive details for in-depth study
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Focus on factual popup data collection
            - Include quantitative metrics when available
            - Highlight successful patterns and trends
            - Prepare data for trend analysis and recommendations
            - Note any seasonal or temporal patterns
            
            Your primary goal is to collect comprehensive popup data that supports trend analysis, location assessment, and strategic planning.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for popup queries
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for popup queries")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.2)
            .build()
            
        return toolCallbackChatOptions
    }
} 