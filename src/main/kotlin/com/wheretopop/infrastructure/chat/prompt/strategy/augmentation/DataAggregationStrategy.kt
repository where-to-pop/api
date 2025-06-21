package com.wheretopop.infrastructure.chat.prompt.strategy.augmentation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.interfaces.area.AreaToolRegistry
import com.wheretopop.interfaces.building.BuildingToolRegistry
import com.wheretopop.interfaces.popup.PopupToolRegistry
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.support.ToolCallbacks
import org.springframework.ai.tool.ToolCallback
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Data aggregation strategy implementation
 * Aggregates and combines information from multiple retrieval sources
 */
@Component
class DataAggregationStrategy(
    private val areaToolRegistry: AreaToolRegistry,
    private val popupToolRegistry: PopupToolRegistry,
    private val buildingToolRegistry: BuildingToolRegistry,
    @Qualifier("searchToolCallbacks")
    private val mcpToolCallbacks: Array<ToolCallback>
) : BaseChatPromptStrategy() {


    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.DATA_AGGREGATION
    }

    /**
     * Returns retrieval aggregation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Synthesize collected context and identify information gaps for user insights.
            
            CORE RESPONSIBILITIES:
            - Organize and integrate retrieval from previous collection steps
            - Identify patterns and connections across area, building, and popup retrieval
            - Assess information completeness against user's specific requirements
            - Fill critical gaps through targeted additional retrieval collection
            
            CONTEXT ANALYSIS APPROACH:
            - Review all previously collected retrieval for relevance and completeness
            - Map retrieval relationships (area ↔ building ↔ popup success patterns)
            - Identify missing pieces crucial for user's augmentation-making
            - Prioritize gap-filling based on business impact
            
            SMART GAP IDENTIFICATION:
            - **Critical Gaps**: Missing retrieval that blocks augmentation-making
            - **Insight Gaps**: Additional context that enhances recommendations
            - **Validation Gaps**: Information needed to confirm hypotheses
            - **Competitive Gaps**: Market intelligence for strategic positioning
            
            TOOL USAGE STRATEGY:
            - Use all tool registries selectively to fill identified gaps only
            - Focus on high-impact missing information rather than comprehensive collection
            - Cross-validate conflicting information through additional sources
            
            RESPONSE GUIDANCE:
            - TARGET AUDIENCE: Final generation generation strategies (human-facing output creators)
            - PURPOSE: Deliver synthesized insights and validated conclusions ready for user presentation
            - FORMAT: Integrated analysis with clear conclusions, confidence levels, and recommendation rationale
            - TONE: Analytical synthesis (like a consulting report executive summary)
            - FOCUS: Connected insights, validated conclusions, augmentation-supporting analysis, risk-benefit assessment
            
            Provide comprehensive analysis that enables direct transformation into user-ready recommendations.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for retrieval aggregation
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {

        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.1)
            .build()
            
        return toolCallbackChatOptions
    }
} 