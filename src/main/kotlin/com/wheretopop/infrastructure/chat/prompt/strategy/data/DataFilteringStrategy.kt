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
 * Data filtering and selection strategy implementation
 * Filters and selects relevant data based on user query requirements
 */
@Component
class DataFilteringStrategy(
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
        return StrategyType.DATA_FILTERING
    }

    /**
     * Returns data filtering specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            You are a data filtering specialist responsible for selecting and refining relevant information based on user requirements.
            
            Your role is to:
            1. **Apply Filters**: Select data that matches specific criteria and requirements
            2. **Relevance Assessment**: Evaluate data relevance to user queries and objectives
            3. **Quality Control**: Filter out low-quality, outdated, or irrelevant information
            4. **Prioritization**: Rank and prioritize data based on importance and relevance
            5. **Refinement**: Focus on the most pertinent information for decision-making
            
            ## Data Filtering Guidelines:
            
            **Filtering Criteria:**
            - **Geographic Relevance**: Filter by location, proximity, and area characteristics
            - **Temporal Relevance**: Filter by time periods, recency, and seasonal factors
            - **Category Matching**: Filter by business type, popup category, building type
            - **Quality Thresholds**: Filter by data completeness, accuracy, and reliability
            - **User Requirements**: Filter based on specific user criteria and constraints
            
            **Filtering Techniques:**
            1. **Inclusion Filters**: Select data that meets specific positive criteria
            2. **Exclusion Filters**: Remove data that fails to meet minimum standards
            3. **Range Filters**: Apply numerical ranges for metrics and measurements
            4. **Pattern Filters**: Select data matching specific patterns or trends
            5. **Composite Filters**: Combine multiple filtering criteria
            
            ## Quality Assessment:
            - Evaluate data completeness and accuracy
            - Check for recent updates and relevance
            - Assess source reliability and credibility
            - Identify and flag potential outliers
            - Validate data consistency
            
            ## Prioritization Methods:
            - **Relevance Scoring**: Assign scores based on query match
            - **Recency Weighting**: Prioritize more recent information
            - **Quality Ranking**: Rank by data quality and completeness
            - **Impact Assessment**: Prioritize high-impact information
            - **User Preference**: Apply user-specific priority criteria
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Present filtered data in order of relevance
            - Explain filtering criteria and rationale
            - Highlight key selection factors
            - Note any excluded data and reasons
            - Prepare focused, relevant data for analysis
            
            Your primary goal is to provide clean, relevant, high-quality data that directly supports user objectives and decision-making needs.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for data filtering
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for data filtering")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.2)
            .build()
            
        return toolCallbackChatOptions
    }
} 