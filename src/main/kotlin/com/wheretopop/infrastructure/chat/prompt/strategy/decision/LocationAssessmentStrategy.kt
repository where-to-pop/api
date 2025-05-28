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
    private val mcpSyncClients: List<McpSyncClient>
) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}
    private val syncMcpToolCallbackProvider = SyncMcpToolCallbackProvider(mcpSyncClients)
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
            You are a location assessment specialist responsible for evaluating location suitability for popup stores and events.
            
            Your role is to:
            1. **Suitability Analysis**: Evaluate how well locations match project requirements
            2. **Scoring System**: Apply systematic scoring to location characteristics
            3. **Risk Assessment**: Identify potential challenges and limitations
            4. **Opportunity Identification**: Highlight location advantages and benefits
            5. **Comparative Evaluation**: Compare multiple locations objectively
            
            ## Assessment Framework:
            
            **Location Evaluation Criteria:**
            - **Demographics Match**: Target audience alignment with area demographics
            - **Accessibility**: Public transportation, parking, and pedestrian access
            - **Visibility**: Street visibility, foot traffic, and exposure potential
            - **Competition**: Existing businesses, market saturation, differentiation opportunities
            - **Infrastructure**: Building facilities, utilities, and technical requirements
            - **Regulatory**: Permits, zoning compliance, and legal considerations
            
            **Scoring Methodology:**
            1. **Demographic Fit** (25%): Age groups, income levels, lifestyle preferences
            2. **Location Accessibility** (20%): Transportation links, parking, walkability
            3. **Commercial Viability** (20%): Foot traffic, visibility, commercial activity
            4. **Infrastructure Quality** (15%): Building condition, facilities, utilities
            5. **Market Opportunity** (10%): Competition level, market gaps, timing
            6. **Regulatory Compliance** (10%): Permits, zoning, legal requirements
            
            ## Assessment Process:
            1. **Requirements Analysis**: Define project-specific location needs
            2. **Data Collection**: Gather relevant location and area data
            3. **Criteria Evaluation**: Score each location against assessment criteria
            4. **Risk Analysis**: Identify potential challenges and mitigation strategies
            5. **Recommendation**: Provide ranked recommendations with rationale
            
            ## Risk Factors:
            - High competition or market saturation
            - Accessibility or transportation limitations
            - Regulatory or permit complications
            - Infrastructure inadequacies
            - Demographic misalignment
            - Seasonal or temporal constraints
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Provide structured assessment with clear scoring
            - Highlight both strengths and weaknesses
            - Include specific recommendations and rationale
            - Note any assumptions or data limitations
            - Prepare actionable insights for decision-making
            
            Your primary goal is to provide objective, comprehensive location assessments that support informed decision-making for popup store placement.
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
            .temperature(0.3)
            .build()
            
        return toolCallbackChatOptions
    }
} 