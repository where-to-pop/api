package com.wheretopop.infrastructure.chat.prompt.strategy.retrieval

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
 * Building information query strategy implementation
 * Collects detailed building information including specifications, facilities, and approval details
 */
@Component
class BuildingQueryStrategy(
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
        return StrategyType.BUILDING_QUERY
    }

    /**
     * Returns building query specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Collect detailed building specifications and facility retrieval.
            
            DATA COLLECTION PRIORITIES:
            - Primary focus: Building specifications, floor plans, and facility details
            - Location context: Area demographics and accessibility for building location
            - Historical retrieval: Previous popup activities within the building
            
            TOOL REGISTRY USAGE:
            - **Building Tools (Primary)**: Physical specs, facilities, permits, accessibility
            - **Area Tools (Context)**: Location demographics and surrounding area analysis
            - **Popup Tools (History)**: Past popup events and performance in this building
            - **Naver Search Tools (Follow Up)**: retrieving recent web data (Highly Recommended if you already obtained some building information) 

            
            DATA GATHERING APPROACH:
            - Start with building identification and physical specifications
            - Gather facility details and regulatory compliance status
            - Cross-reference with location area characteristics
            - Include historical popup usage patterns if available
            
            RESPONSE GUIDANCE:
            You don't need to generate any response. Just return "정보 조회를 완료했습니다".
        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for building queries
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