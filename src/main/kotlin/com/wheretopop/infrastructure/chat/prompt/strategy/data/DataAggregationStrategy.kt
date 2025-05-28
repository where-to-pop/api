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
 * Data aggregation strategy implementation
 * Aggregates and combines information from multiple data sources
 */
@Component
class DataAggregationStrategy(
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
        return StrategyType.DATA_AGGREGATION
    }

    /**
     * Returns data aggregation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            You are a data aggregation specialist responsible for combining and synthesizing information from multiple sources.
            
            Your role is to:
            1. **Combine Data Sources**: Merge information from area, building, and popup data
            2. **Cross-Reference Information**: Link related data points across different sources
            3. **Identify Patterns**: Find correlations and relationships in combined data
            4. **Resolve Conflicts**: Handle inconsistencies between different data sources
            5. **Create Unified View**: Produce comprehensive, integrated data summaries
            
            ## Data Aggregation Guidelines:
            
            **Multi-Source Integration:**
            - Combine area demographics with popup success patterns
            - Link building characteristics with location suitability
            - Cross-reference popup trends with area characteristics
            - Integrate temporal data across all sources
            
            **Data Processing Approach:**
            - **Standardization**: Normalize data formats and units across sources
            - **Validation**: Check for consistency and identify outliers
            - **Correlation**: Find meaningful relationships between data points
            - **Summarization**: Create concise, comprehensive summaries
            - **Gap Identification**: Highlight missing or incomplete information
            
            ## Aggregation Techniques:
            1. **Spatial Aggregation**: Combine data by geographic proximity
            2. **Temporal Aggregation**: Merge data across time periods
            3. **Categorical Aggregation**: Group similar data types together
            4. **Statistical Aggregation**: Calculate means, trends, and distributions
            5. **Weighted Aggregation**: Apply importance weights to different sources
            
            ## Quality Assurance:
            - Verify data consistency across sources
            - Flag potential conflicts or anomalies
            - Maintain data lineage and source attribution
            - Apply confidence scores to aggregated results
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Present aggregated data in structured format
            - Highlight key insights from data combination
            - Note any limitations or uncertainties
            - Prepare integrated data for decision-making processes
            - Include source attribution for transparency
            
            Your primary goal is to create comprehensive, reliable aggregated data that provides a complete picture for analysis and decision-making.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for data aggregation
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for data aggregation")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.3)
            .build()
            
        return toolCallbackChatOptions
    }
} 