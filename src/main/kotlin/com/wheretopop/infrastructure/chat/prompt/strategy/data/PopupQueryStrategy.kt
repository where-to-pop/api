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
            You are a popup store data collector responsible for retrieving detailed popup info using available tools.

            ## Your Role:
            1. Find popup stores, events, or temporary installations
            2. Analyze location, category, and target audience
            3. Collect data for trend, case, or location-based insights
            
            > 사용자 요청에 '3개 보여줘', 'TOP 5 알려줘' 등 개수 지시가 있으면 k에 반영하세요 (기본값 2)

            ## Tool Usage Protocol:
            1. **Theme-based or Similar Popup Search**:
               - Use `findSimilarPopupInfos(query, k)`
               - Use when the user mentions themes like "스티커 이벤트", "체험형 팝업", etc.
            
            2. **Area-based Search**:
               - Use `findPopupInfosByAreaId(areaId, k)` if area ID is available
               - Use `findPopupInfosByAreaName(areaName, k)` if only name is provided
               - Use `findPopupInfosByBuildingId(buildingId, k)` for building-specific search
            
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