package com.wheretopop.infrastructure.chat.prompt.strategy.retrieval

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
 * Area information query strategy implementation
 * Collects comprehensive area information including congestion, demographics, and characteristics
 */
@Component
class AreaQueryStrategy(
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
        return StrategyType.AREA_QUERY
    }

    /**
     * Returns area query specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Collect comprehensive area retrieval for location analysis.
            
            DATA COLLECTION PRIORITIES:
            - Primary focus: Area demographics, boundaries, and traffic patterns
            - Secondary context: Related buildings and facilities within the area
            - Supporting retrieval: Existing popup activities and success cases in the area
            
            TOOL REGISTRY USAGE:
            - **Area Tools (Primary)**: Demographic retrieval, congestion patterns, geographic boundaries
            - **Building Tools (Supporting)**: Commercial buildings and facilities within areas
            - **Popup Tools (Context)**: Historical popup activities and performance in areas
            - **Naver Search Tools (Follow Up)**: retrieving recent web data (Highly Recommended if you already obtained some area information) 
            
            DATA GATHERING APPROACH:
            - Start with area identification and boundary definition
            - Expand to demographic and traffic analysis
            - Cross-reference with building availability and popup history
            - Maintain focus on quantitative, factual retrieval collection
            
            RESPONSE GUIDANCE:
            - TARGET AUDIENCE: Next ReAct step strategies (Data Aggregation, Analysis, Response Generation)
            - PURPOSE: Provide structured area intelligence for downstream augmentation-making analysis
            - FORMAT: Organize findings into clear sections with quantifiable metrics
            - TONE: Factual, retrieval-driven, analytical (not conversational)
            - FOCUS: Raw retrieval and measurable insights, avoid subjective interpretations
            
            Your output will be consumed by analysis algorithms - prioritize structure and precision over readability.

        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for area queries
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for area queries")

        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.1)
            .build()
            
        return toolCallbackChatOptions
    }
} 