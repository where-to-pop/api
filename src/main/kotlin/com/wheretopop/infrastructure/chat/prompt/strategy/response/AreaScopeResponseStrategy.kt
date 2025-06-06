package com.wheretopop.infrastructure.chat.prompt.strategy.response

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
 * Area scope definition response strategy implementation
 * Generates responses for area scope definition queries with geographical boundaries
 */
@Component
class AreaScopeResponseStrategy(
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
        return StrategyType.AREA_SCOPE_RESPONSE
    }

    /**
     * Returns area scope response specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Define geographical area boundaries and scope for pop-up site selection.
            
            FOCUS AREAS:
            - Exact boundary definition with key landmarks
            - Commercial district characteristics and sub-zones
            - Transit accessibility and pedestrian flow patterns
            - Peak activity times and seasonal variations
            
            FORMAT:
            1. 경계 정의: Clear boundary description with landmarks
            2. 상권 특성: Business district characteristics  
            3. 접근성: Transit and foot traffic analysis
            4. 시간대별 특성: Peak times and seasonal patterns
            
            Emphasize practical boundary understanding for site selection decisions.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for area scope responses
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for area scope responses")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.4)
            .build()
            
        return toolCallbackChatOptions
    }
} 