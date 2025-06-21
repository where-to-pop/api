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
            SPECIALTY: Collect popup store cases, success patterns, and performance retrieval.
            
            DATA COLLECTION PRIORITIES:
            - Primary focus: Popup case studies, performance metrics, and success patterns
            - Location correlation: Cross-reference popup performance with area characteristics  
            - Facility requirements: Match popup needs with building capabilities
            
            TOOL REGISTRY USAGE:
            - **Popup Tools (Primary)**: Case studies, brand analysis, performance metrics, trends
            - **Area Tools (Correlation)**: Location demographics where popups succeeded/failed
            - **Building Tools (Requirements)**: Facility specifications for successful popup setups
            - **Naver Search Tools (Follow Up)**: retrieving recent web data (Highly Recommended if you already obtained some building information) 

            POPUP TOOL:
            - findPopupInfosByFilters: Parameters: 
                - query: String (required) – Describe desired popup characteristics
                - k: Int = 3 – Number of results to return
                Optional parameters – return null if unknown:
                - areaId: Long? – Optional area ID
                - buildingId: Long? – Optional building ID
                - areaName: String? – Optional, must be one of predefined area names
                - ageGroup: String? – Optional, must be one of: TEEN_AND_UNDER, TWENTIES, THIRTIES, FORTIES, FIFTY_AND_OVER
                - category: String? – Optional, must be one of: FASHION, FOOD_AND_BEVERAGE, BEAUTY, ART, CHARACTER, MEDIA, OTHER
                
            - findSimilarPopupInfos: Parameters: query: String (required), k: Int – Use query to describe desired popup characteristics and k for number of results.
            - findPopupInfosByAreaId: Parameters: areaId: Long, query: String (required), k: Int – Use areaId for a specific area ID, query for more specific searches and k for number of results.
            - findPopupInfosByBuildingId: Parameters: buildingId: Long, query: String (required), k: Int – Use buildingId for a specific building ID, query for more specific searches and k for number of results.
            - findPopupInfosByAreaName: Parameters: areaName: String, query: String (required), k: Int – Use areaName to specify area, query for more specific searches and k for number of results.
            - findPopupInfosByTargetAgeGroup: Parameters: ageGroup: String, query: String (required), k: Int – Use ageGroup from TEEN_AND_UNDER, TWENTIES, THIRTIES, FORTIES, FIFTY_AND_OVER and query for more specific searches and k for number of results.
            - findPopupInfosByCategory: Parameters: category: String, query: String (required), k: Int – Use category from FASHION, FOOD_AND_BEVERAGE, BEAUTY, ART, CHARACTER, MEDIA, OTHER and query for more specific searches and k for number of results.

            PREDEFINED AREA NAME:
            "강남 MICE 관광특구", "동대문 관광특구", "명동 관광특구", "이태원 관광특구", "잠실 관광특구", "홍대 관광특구", "강남역", "건대입구역", "고속터미널역", "사당역", "서울역", "선릉역", "신촌·이대역", "충정로역", "합정역", "혜화역", "가로수길", "북촌한옥마을", "서촌", "성수카페거리", "압구정로데오거리", "여의도", "연남동", "영등포 타임스퀘어", "용리단길", "인사동·익선동", "해방촌·경리단길", "광화문광장"
            
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
            
            Provide pattern-rich retrieval that enables success probability calculations and risk assessment.
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
