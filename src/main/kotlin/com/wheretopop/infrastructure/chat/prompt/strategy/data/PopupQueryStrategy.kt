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
            SPECIALTY: Collect popup store cases, success patterns, and performance data.
            
            DATA COLLECTION PRIORITIES:
            - Primary focus: Popup case studies, performance metrics, and success patterns
            - Location correlation: Cross-reference popup performance with area characteristics  
            - Facility requirements: Match popup needs with building capabilities
            
            TOOL REGISTRY USAGE:
            - **Popup Tools (Primary)**: Case studies, brand analysis, performance metrics, trends
            - **Area Tools (Correlation)**: Location demographics where popups succeeded/failed
            - **Building Tools (Requirements)**: Facility specifications for successful popup setups
            
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