package com.wheretopop.infrastructure.chat.prompt.strategy.data

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.interfaces.area.AreaToolRegistry
import com.wheretopop.interfaces.building.BuildingToolRegistry
import com.wheretopop.interfaces.popup.PopupToolRegistry
import mu.KotlinLogging
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.support.ToolCallbacks
import org.springframework.ai.tool.ToolCallback
import org.springframework.beans.factory.annotation.Qualifier
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
    @Qualifier("searchToolCallbacks")
    private val mcpToolCallbacks: Array<ToolCallback>

) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}

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
            SPECIALTY: Collect popup store cases, success patterns, and performance data.
            
            DATA COLLECTION PRIORITIES:
            - Primary focus: Popup case studies, performance metrics, and success patterns
            - Location correlation: Cross-reference popup performance with area characteristics  
            - Facility requirements: Match popup needs with building capabilities
            
            TOOL REGISTRY USAGE:
            - **Popup Tools (Primary)**: Case studies, brand analysis, performance metrics, trends
            - **Area Tools (Correlation)**: Location demographics where popups succeeded/failed
            - **Building Tools (Requirements)**: Facility specifications for successful popup setups
            
            POPUP TOOL:
            - findSimilarPopupInfos: Parameters: query: String, k: Int - Use query to describe desired popup characteristics (e.g., 'sticker event', 'cooking class') and k for the number of results.
            - findPopupInfosByAreaId: Parameters: areaId: Long, query: String, k: Int - Use areaId for a specific area ID, and optionally query for more specific searches and k for the number of results.
            - findPopupInfosByBuildingId: Parameters: buildingId: Long, query: String, k: Int - Use buildingId for a specific building ID, and optionally query for more specific searches and k for the number of results.
            - findPopupInfosByAreaName: Parameters: areaName: String, query: String, k: Int - Use areaName to specify the area (e.g., '홍대, 건대, 강남'), and optionally query for more specific searches and k for the number of results.
            - findPopupInfosByTargetAgeGroup: Parameters: ageGroup: String, query: String, k: Int - Use ageGroup from TEEN_AND_UNDER, TWENTIES, THIRTIES, FORTIES, FIFTY_AND_OVER, and optionally query for more specific searches and k for the number of results.
            - findPopupInfosByCategory: Parameters: category: String, query: String, k: Int - Use category from FASHION, FOOD_AND_BEVERAGE, BEAUTY, ART, CHARACTER, MEDIA, OTHER, and optionally query for more specific searches and k for the number of results.
            
            DATA GATHERING APPROACH:
            - Start with popup case identification and performance analysis
            - Cross-reference success cases with location characteristics
            - Identify facility requirements and operational constraints
            - Build patterns connecting location, building, and success factors
            
            RESPONSE GUIDANCE:
            - TARGET AUDIENCE: Pattern analysis and recommendation synthesis strategies
            - PURPOSE: Supply proven success patterns and benchmarks for predictive modeling
            - FORMAT: Case studies with quantified outcomes, pattern identification, success correlations
            - TONE: Evidence-based, pattern-focused (like market research findings)
            - FOCUS: Performance metrics, success factors, failure patterns, and predictive indicators
            
            Provide pattern-rich data that enables success probability calculations and risk assessment.
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