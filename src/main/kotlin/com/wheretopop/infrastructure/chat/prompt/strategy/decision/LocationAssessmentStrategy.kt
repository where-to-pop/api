package com.wheretopop.infrastructure.chat.prompt.strategy.decision

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
 * Location suitability assessment strategy implementation
 * Assesses location suitability based on project requirements and constraints
 */
@Component
class LocationAssessmentStrategy(
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
        return StrategyType.LOCATION_ASSESSMENT
    }

    /**
     * Returns location assessment specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Evaluate location suitability against user requirements and business objectives.
            
            ASSESSMENT PRIORITIES:
            - Match location characteristics with user's target audience and brand positioning
            - Evaluate business viability through demographic, accessibility, and competition analysis
            - Identify critical success factors and potential deal-breakers
            - Provide objective scoring based on quantifiable criteria
            
            EVALUATION FRAMEWORK:
            - **Market Fit**: Target audience alignment and demographic compatibility
            - **Accessibility**: Transportation, parking, and foot traffic accessibility
            - **Competition**: Market saturation, differentiation opportunities, pricing pressure
            - **Infrastructure**: Building suitability, facility requirements, operational constraints
            - **Risk Factors**: Regulatory issues, seasonal variations, market uncertainties
            
            CONTEXT UTILIZATION:
            - Synthesize area demographics, building specs, and popup case studies
            - Cross-reference user requirements with location realities
            - Apply proven success patterns from similar popup cases
            - Balance quantitative data with qualitative market insights
            
            RESPONSE GUIDANCE:
            - TARGET AUDIENCE: Final response generation strategies (recommendation and presentation layers)
            - PURPOSE: Deliver decisive suitability assessment with clear recommendation rationale
            - FORMAT: Scored evaluation with pros/cons analysis, risk assessment, and confidence levels
            - TONE: Strategic assessment (like investment evaluation or business case analysis)
            - FOCUS: Suitability scores, decision factors, risk-benefit analysis, actionable recommendations
            
            Provide definitive assessment conclusions that enable confident recommendation generation.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for location assessment
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for location assessment")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.1)
            .build()
            
        return toolCallbackChatOptions
    }
} 