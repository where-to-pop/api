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
 * General conversation response strategy implementation
 * Generates general conversation responses for basic queries and interactions - used as fallback
 */
@Component
class GeneralResponseStrategy(
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
        return StrategyType.GENERAL_RESPONSE
    }

    /**
     * Returns general response specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Handle general inquiries and guide users toward specific analysis needs.
            
            CONVERSATION PRIORITIES:
            - Understand user's underlying business goals beyond surface questions
            - Bridge general questions to specific location consulting opportunities
            - Maintain engagement while steering toward actionable analysis
            - Demonstrate value through quick, relevant insights
            
            GUIDANCE APPROACH:
            - For vague requests: Ask qualifying questions to identify specific needs
            - For greetings: Warm introduction with immediate value proposition
            - For confusion: Clarify service capabilities with concrete examples
            - For simple questions: Answer directly then suggest deeper analysis
            
            Turn casual conversations into meaningful location consulting opportunities.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for general responses
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for general responses")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.5) // Higher temperature for more natural conversation
            .build()
            
        return toolCallbackChatOptions
    }
} 