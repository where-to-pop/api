package com.wheretopop.infrastructure.chat.prompt.strategy.decision

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.interfaces.area.AreaToolRegistry
import com.wheretopop.interfaces.building.BuildingToolRegistry
import com.wheretopop.interfaces.popup.PopupToolRegistry
import io.modelcontextprotocol.client.McpSyncClient
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.support.ToolCallbacks
import org.springframework.stereotype.Component

/**
 * ReAct execution planner - multi-step planning using ReAct framework
 */
@Component
class ReActExecutionPlanningStrategy(
    private val areaToolRegistry: AreaToolRegistry,
    private val popupToolRegistry: PopupToolRegistry,
    private val buildingToolRegistry: BuildingToolRegistry,
    private val mcpSyncClients: List<McpSyncClient>
) : BaseChatPromptStrategy() {
    private val syncMcpToolCallbackProvider = SyncMcpToolCallbackProvider(mcpSyncClients)
    private val mcpToolCallbacks = syncMcpToolCallbackProvider.toolCallbacks
    

    override fun getType(): StrategyType {
        return StrategyType.REACT_PLANNER
    }


    override fun getSystemPrompt(): String {
        val dataCollectionStrategies = StrategyType.getDataCollectionStrategies()
            .filter { it != StrategyType.REACT_PLANNER }
            .joinToString("\n") { "- ${it.id}: ${it.description}" }
        
        val dataProcessingStrategies = StrategyType.getDataProcessingStrategies()
            .joinToString("\n") { "- ${it.id}: ${it.description}" }
        
        val decisionMakingStrategies = StrategyType.getDecisionMakingStrategies()
            .filter { it != StrategyType.REACT_PLANNER }
            .joinToString("\n") { "- ${it.id}: ${it.description}" }
        
        val responseGenerationStrategies = StrategyType.getResponseGenerationStrategies()
            .joinToString("\n") { "- ${it.id}: ${it.description}" }
        
        return """
            You are an adaptive execution planner for WhereToPop requirements.
            
            You receive pre-analyzed requirements with complexity levels and create appropriate execution plans.
            
            ## Available Strategy Categories:
            
            $dataCollectionStrategies
            
            $dataProcessingStrategies
            
            $decisionMakingStrategies
            
            $responseGenerationStrategies
            
            ## Complexity-Based Planning:
            
            ### MODERATE Complexity:
            - Use 1-2 data collection steps
            - Simple analysis or direct response
            - Efficient, focused approach
            
            ### COMPLEX Complexity:
            - Multi-source data collection
            - Deep analysis and comparison
            - Comprehensive recommendation with rationale
            
            ## RAG Framework (MANDATORY):
            1. **R (Retrieval)**: Collect relevant data
            2. **A (Augmentation)**: Process and analyze (if needed)
            3. **G (Generation)**: Generate response (MUST be final step)
            
            ## Response Format:
            ```json
            {
                "thought": "Analysis of requirements and execution approach based on complexity",
                "actions": [
                    {
                        "step": 1,
                        "strategy": "area_query",
                        "purpose": "Data collection objective",
                        "reasoning": "Why this strategy is needed",
                        "recommended_tools": ["relevant_tools"],
                        "tool_sequence": "Execution workflow",
                        "expected_output": "Expected results",
                        "dependencies": []
                    },
                    {
                        "step": 2,
                        "strategy": "general_response",
                        "purpose": "Response generation",
                        "reasoning": "Final synthesis and presentation",
                        "recommended_tools": [],
                        "tool_sequence": "Response generation",
                        "expected_output": "User-ready answer",
                        "dependencies": [1]
                    }
                ],
                "observation": "Plan validation and efficiency assessment"
            }
            ```
            
            ## Guidelines:
            - ALWAYS end with a Response Generation strategy
            - Adapt plan complexity to the provided complexity level
            - Ensure efficient execution with minimal unnecessary steps
            - Focus on delivering value to the user
        """.trimIndent()
    }
    

    override fun createPrompt(userMessage: String): Prompt {
        val messages: MutableList<Message> = mutableListOf()
        
        messages.add(SystemMessage(getSystemPrompt()))
        
        val planningPrompt = """
            Create a comprehensive execution plan for this complex requirement:
            
            $userMessage
            
            Design a multi-step RAG execution plan with proper data collection, analysis, and response generation phases.
        """.trimIndent()
        
        messages.add(UserMessage(planningPrompt))
        
        return Prompt(messages)
    }
    

    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.2)
            .build()
            
        return toolCallbackChatOptions
    }
} 