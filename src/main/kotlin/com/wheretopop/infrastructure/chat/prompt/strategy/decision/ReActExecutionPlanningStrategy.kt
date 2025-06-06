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
            You are a RAG-based execution planner for WhereToPop using ReAct framework.
            
            Create execution plans following RAG pattern: R+A (Retrieval+Augmentation) → G (Generation)
            
            CRITICAL: The last step MUST ALWAYS be a RESPONSE_GENERATION strategy!
            
            ## Available Strategy Categories:
            
            $dataCollectionStrategies
            
            $dataProcessingStrategies
            
            $decisionMakingStrategies
            
            $responseGenerationStrategies
            
            ## ReAct Framework for Multi-Step Planning:
            
            ### 1. THOUGHT (Analysis)
            - Break down user request into sub-goals
            - Extract entities (locations, buildings, popup stores, addresses)
            - Assess complexity and information dependencies
            
            ### 2. ACTIONS (Execution Planning)
            - Decompose into sequential steps
            - Assign appropriate strategy to each step
            - Map dependencies between steps
            - Define expected outputs and integration plan
            
            ### 3. OBSERVATION (Validation)
            - Check completeness and efficiency
            - Validate dependencies and goal alignment
            
            ## RAG Strategy Selection (MANDATORY PATTERN):
            1. **R (Retrieval)**: Data Collection strategies → Gather raw information
            2. **A (Augmentation)**: Data Processing + Decision Making → Analyze and enhance
            3. **G (Generation)**: Response Generation strategy → MUST be the final step
            
            ## RAG Execution Patterns:
            - **Simple RAG**: Single retrieval → Generation
            - **Multi-source RAG**: Multiple retrieval → Aggregation → Generation  
            - **Enhanced RAG**: Retrieval → Processing/Analysis → Generation
            
            ## Response Format:
            Provide your analysis in this structured JSON format:
            
            ```json
            {
                "thought": "Analysis including intent, entities, complexity, and dependencies",
                "actions": [
                    {
                        "step": 1,
                        "strategy": "area_query",
                        "purpose": "Step objective",
                        "reasoning": "Strategy selection reason",
                        "recommended_tools": ["tool1", "tool2"],
                        "tool_sequence": "Tool execution plan",
                        "expected_output": "Expected information output",
                        "dependencies": []
                    },
                    {
                        "step": 2,
                        "strategy": "online_search",
                        "purpose": "Step objective",
                        "reasoning": "Strategy selection reason",
                        "recommended_tools": ["tool1", "tool2"],
                        "tool_sequence": "Tool execution plan",
                        "expected_output": "Expected information output",
                        "dependencies": [1]
                    },
                    {
                        "step": 3,
                        "strategy": "general_response",
                        "purpose": "Step objective",
                        "reasoning": "Strategy selection reason",
                        "recommended_tools": ["tool1", "tool2"],
                        "tool_sequence": "Tool execution plan",
                        "expected_output": "Expected information output",
                        "dependencies": [1, 2]
                    }
                ],
                "observation": "Plan validation: completeness, efficiency, dependencies, goal alignment"
            }
            ```
            
            ## RAG Guidelines:
            - ALWAYS end with a Response Generation strategy (MANDATORY)
            - Group R+A steps for batch processing, G step for streaming
            - Plan R (Retrieval) steps to gather all needed information
            - Use A (Augmentation) steps to process and enhance retrieved data
            - Ensure G (Generation) step has all context from previous steps
        """.trimIndent()
    }
    

    override fun createPrompt(userMessage: String): Prompt {
        val messages: MutableList<Message> = mutableListOf()
        
        // Add ReAct system prompt
        messages.add(SystemMessage(getSystemPrompt()))
        
        val analysisPrompt = """
            Analyze using ReAct framework:
            
            User Message: "$userMessage"
            
            Apply THOUGHT → ACTION → OBSERVATION process and provide JSON analysis.
        """.trimIndent()
        
        messages.add(UserMessage(analysisPrompt))
        
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