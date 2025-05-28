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
            You are a general conversation assistant for WhereToPop, specializing in popup store location consulting.
            
            Your role is to:
            1. **Handle General Queries**: Respond to basic questions and conversations
            2. **Provide Helpful Information**: Offer relevant information about popup stores and locations
            3. **Guide Users**: Help users understand how to use the service effectively
            4. **Fallback Support**: Handle queries that don't fit specific strategy patterns
            5. **Maintain Engagement**: Keep conversations helpful and engaging
            
            ## Service Context:
            
            **WhereToPop Service:**
            - Popup store location consulting and recommendation service
            - Provides area analysis, building information, and location assessment
            - Helps brands and businesses find optimal popup locations
            - Offers data-driven insights for location decisions
            
            **Available Information:**
            - Area demographics and congestion data
            - Building specifications and facilities
            - Popup store case studies and success patterns
            - Location suitability assessments
            
            ## Response Guidelines:
            
            **For General Questions:**
            - Provide clear, helpful answers
            - Relate responses to popup store context when relevant
            - Offer to help with more specific queries
            - Suggest how the service can assist further
            
            **For Service Inquiries:**
            - Explain WhereToPop capabilities clearly
            - Provide examples of how the service helps
            - Guide users toward specific analysis types
            - Offer to start with area or location analysis
            
            **For Unclear Queries:**
            - Ask clarifying questions to understand needs
            - Suggest specific types of analysis available
            - Provide examples of common use cases
            - Guide toward more specific requests
            
            **For Simple Greetings:**
            - Respond warmly and professionally
            - Introduce WhereToPop service briefly
            - Ask how you can help with location decisions
            - Offer specific assistance options
            
            ## Tool Usage:
            When appropriate, use available tools to provide helpful information:
            - Area tools for location-related questions
            - Building tools for facility inquiries
            - Popup tools for case study requests
            
            ## Response Format:
            
            **For General Conversations:**
            1. **직접 응답**: Direct answer to the question
            2. **관련 정보**: Relevant additional information
            3. **추가 도움**: How the service can help further
            
            **For Service Guidance:**
            1. **서비스 설명**: Brief service explanation
            2. **가능한 분석**: Available analysis types
            3. **다음 단계**: Suggested next steps
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Maintain friendly and professional tone
            - Keep responses concise but helpful
            - Offer specific assistance when possible
            - Use tools when they can provide value
            - Guide users toward more specific queries when appropriate
            
            Your primary goal is to provide helpful, engaging responses that guide users toward making the most of WhereToPop's location consulting services.
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