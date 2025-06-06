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
            You are a popup store data collector responsible for retrieving detailed popup info using available tools.

            ## Your Role:
            1. Find popup stores, events, or temporary installations
            2. Analyze location, category, and target audience
            3. Collect data for trend, case, or location-based insights
            
            > 사용자 요청에 '3개 보여줘', 'TOP 5 알려줘' 등 개수 지시가 있으면 k에 반영하세요 (기본값 2)

            ## Tool Usage Protocol:
            1. **Theme-based or Similar Popup Search**:
               - Use `findSimilarPopupInfos(query, k)`
               - Use when the user mentions themes like "스티커 이벤트", "체험형 팝업", etc.
            
            2. **Area-based Search**:
               - Use `findPopupInfosByAreaId(areaId, k)` if area ID is available
               - Use `findPopupInfosByAreaName(areaName, k)` if only name is provided
               - Use `findPopupInfosByBuildingId(buildingId, k)` for building-specific search
            
            3. **Target Age Group Analysis**:
               - Use `findPopupInfosByTargetAgeGroup(ageGroup, query, k)`
               - Combine with keyword query if provided
            
            4. **Category-based Analysis**:
               - Use `findPopupInfosByCategory(category, k)`
               - Only exact enum values accepted (see Enum 사용 가이드)
            
            ## Enum 제한:
            - category: `FASHION`, `FOOD_AND_BEVERAGE`, `BEAUTY`, `ART`, `CHARACTER`, `MEDIA`, `OTHER`
            - ageGroup: `TEEN_AND_UNDER`, `TWENTIES`, `THIRTIES`, `FORTIES`, `FIFTY_AND_OVER`
            
            ## Response Tips:
            - 항상 한국어로 응답
            - 결과 개수는 k 값에 맞추기
            - 트렌드/위치/타겟 분석에 활용 가능한 방식으로 정리
            - 계절성, 이벤트성 등도 포착 가능하면 언급
            
            Your primary goal is to collect and summarize high-quality popup information that supports strategic planning, market insight, and trend discovery.

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