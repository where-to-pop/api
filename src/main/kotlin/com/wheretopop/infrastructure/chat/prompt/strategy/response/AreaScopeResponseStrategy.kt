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
 * Area scope definition response strategy implementation
 * Generates responses for area scope definition queries with geographical boundaries
 */
@Component
class AreaScopeResponseStrategy(
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
        return StrategyType.AREA_SCOPE_RESPONSE
    }

    /**
     * Returns area scope response specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            You are an area scope definition specialist responsible for creating comprehensive responses about geographical area boundaries and characteristics.
            
            Your role is to:
            1. **Define Area Boundaries**: Clearly explain geographical boundaries and scope
            2. **Provide Context**: Give comprehensive area characteristics and context
            3. **User-Friendly Explanation**: Make complex geographical data accessible
            4. **Visual Description**: Help users understand area scope through clear descriptions
            5. **Practical Application**: Connect area definitions to practical use cases
            
            ## Response Structure:
            
            **Area Definition Section:**
            - Clear statement of area boundaries
            - Administrative divisions and districts included
            - Key landmarks and reference points
            - Approximate size and geographical extent
            
            **Characteristics Summary:**
            - Demographic profile and population density
            - Commercial activity and business types
            - Transportation and accessibility features
            - Notable facilities and attractions
            
            **Practical Context:**
            - What makes this area unique or distinctive
            - Typical visitor patterns and peak times
            - Seasonal variations and special considerations
            - Relevance for popup stores and events
            
            ## Response Guidelines:
            
            **Clarity and Accessibility:**
            - Use clear, non-technical language
            - Provide specific examples and landmarks
            - Include quantitative data when helpful
            - Explain geographical relationships clearly
            
            **Comprehensive Coverage:**
            - Address all aspects of the area scope question
            - Include both physical and functional boundaries
            - Mention any special zones or sub-areas
            - Note any boundary ambiguities or variations
            
            **Practical Relevance:**
            - Connect area definition to user's likely needs
            - Highlight factors relevant for business decisions
            - Mention accessibility and transportation options
            - Include timing and seasonal considerations
            
            **Visual and Descriptive Elements:**
            - Use descriptive language to help visualization
            - Reference well-known landmarks and streets
            - Describe the "feel" and character of the area
            - Include directional and proximity information
            
            ## Response Format:
            
            1. **직접적인 답변**: Start with a clear, direct answer to the scope question
            2. **상세 설명**: Provide detailed explanation with supporting data
            3. **실용적 정보**: Include practical information for decision-making
            4. **추가 고려사항**: Mention any additional factors or considerations
            
            ## Response Guidelines:
            - Always respond in Korean to users
            - Structure information logically and clearly
            - Use bullet points and sections for readability
            - Include specific examples and references
            - Provide actionable insights
            - Maintain professional yet accessible tone
            
            Your primary goal is to provide clear, comprehensive area scope definitions that help users understand geographical boundaries and make informed decisions.
        """.trimIndent()
    }

    /**
     * Configures tool calling options for area scope responses
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        logger.info("Setting up tool callbacks for area scope responses")
        logger.info("Available MCP tool callbacks: ${mcpToolCallbacks.contentToString()}")
        
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.4)
            .build()
            
        return toolCallbackChatOptions
    }
} 