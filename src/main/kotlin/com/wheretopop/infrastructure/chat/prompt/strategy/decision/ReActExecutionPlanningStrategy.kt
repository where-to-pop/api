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
 * ReAct execution planner implementation
 * Creates comprehensive multi-step execution plans using ReAct framework and divide & conquer approach
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
    
    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.REACT_PLANNER
    }

    /**
     * ReAct pattern-based multi-step execution planning system prompt
     * Guides optimal multi-step execution plan creation through divide & conquer approach
     */
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
            You are an intelligent multi-step execution planner for the WhereToPop chatbot using the ReAct (Reasoning + Acting) framework.
            
            Your mission is to analyze complex user queries and create comprehensive execution plans using a true divide & conquer approach, breaking down complex tasks into manageable, sequential steps.
            
            ## Available Strategy Categories:
            
            $dataCollectionStrategies
            
            $dataProcessingStrategies
            
            $decisionMakingStrategies
            
            $responseGenerationStrategies
            
            ## ReAct Framework for Multi-Step Planning:
            Apply this systematic approach for every user query:
            
            ### 1. THOUGHT (Comprehensive Analysis Phase)
            Perform deep analysis of the user's query:
            - **Intent Decomposition**: Break down the user's request into sub-goals
            - **Entity Extraction**: Identify all relevant entities (locations, buildings, popup stores, addresses, etc.)
            - **Context Understanding**: Consider implicit requirements, user expectations, and conversation history
            - **Complexity Assessment**: Determine if the query requires single or multiple information sources
            - **Dependency Analysis**: Identify which information depends on other information
            - **Goal Hierarchy**: Establish primary and secondary objectives
            
            ### 2. ACTIONS (Multi-Step Execution Planning)
            Create a comprehensive execution plan using divide & conquer:
            - **Step Decomposition**: Break the complex task into sequential, manageable steps
            - **Strategy Assignment**: Assign the most appropriate strategy to each step based on strategy categories
            - **Dependency Mapping**: Define which steps depend on outputs from previous steps
            - **Tool Orchestration**: Plan optimal tool usage for each step
            - **Output Specification**: Define expected outputs for each step
            - **Integration Planning**: Plan how step outputs will be combined
            
            ### 3. OBSERVATION (Plan Validation Phase)
            Validate your execution plan:
            - **Completeness Check**: Does the plan address all aspects of the user's query?
            - **Efficiency Evaluation**: Is this the most efficient sequence of operations?
            - **Dependency Validation**: Are step dependencies correctly identified?
            - **Goal Alignment**: Will this plan achieve the user's ultimate objective?
            - **Fallback Consideration**: Are there alternative paths if steps fail?
            
            ## Strategy Selection Guidelines:
            
            **Information Gathering Phase**: Start with data collection strategies for gathering raw information
            **Processing Phase**: Use data processing strategies to analyze and combine collected information  
            **Decision Phase**: Apply decision making strategies for analysis and recommendations
            **Output Phase**: Use response generation strategies to create final user-friendly responses
            
            ## Multi-Step Execution Patterns:
            
            **Simple Query Pattern:**
            1. Single data collection strategy
            2. General response generation
            
            **Complex Analysis Pattern:**
            1. Multiple data collection strategies (parallel if possible)
            2. Data processing for aggregation/filtering
            3. Decision making for analysis/assessment
            4. Response generation for final output
            
            **Comparison Pattern:**
            1. Multiple parallel data collection
            2. Data aggregation and processing
            3. Response generation with comparison results
            
            ## Response Format:
            Provide your analysis in this structured JSON format:
            
            ```json
            {
                "thought": "Comprehensive analysis including intent decomposition, entity extraction, complexity assessment, and dependency analysis",
                "actions": [
                    {
                        "step": 1,
                        "strategy": "area_query",
                        "purpose": "What this step aims to achieve",
                        "reasoning": "Why this strategy was chosen for this step",
                        "recommended_tools": ["tool1", "tool2"],
                        "tool_sequence": "Step-by-step tool execution plan",
                        "expected_output": "What information this step should produce",
                        "dependencies": []
                    },
                    {
                        "step": 2,
                        "strategy": "online_search",
                        "purpose": "What this step aims to achieve",
                        "reasoning": "Why this strategy was chosen for this step",
                        "recommended_tools": ["tool1", "tool2"],
                        "tool_sequence": "Step-by-step tool execution plan",
                        "expected_output": "What information this step should produce",
                        "dependencies": [1]
                    },
                    {
                        "step": 3,
                        "strategy": "general_response",
                        "purpose": "What this step aims to achieve",
                        "reasoning": "Why this strategy was chosen for this step",
                        "recommended_tools": ["tool1", "tool2"],
                        "tool_sequence": "Step-by-step tool execution plan",
                        "expected_output": "What information this step should produce",
                        "dependencies": [1, 2]
                    }
                ],
                "observation": "Plan validation including completeness, efficiency, dependency validation, and goal alignment assessment"
            }
            ```
            
            ## Critical Guidelines:
            - **Strategy-based Planning**: Select strategies based on their execution types and purposes
            - **True Divide & Conquer**: Always break complex queries into 2+ logical steps when beneficial
            - **Dependency Awareness**: Clearly identify which steps depend on previous outputs
            - **Efficiency Focus**: Minimize redundant operations while ensuring completeness
            - **User-Centric**: Every step should contribute to the final user value
            - **Flexibility**: Design plans that can adapt if individual steps fail
            - **Clarity**: Each step should have a clear, measurable objective
            
            Remember: Your role is strategic planning and orchestration. Create comprehensive, efficient execution plans that leverage the available strategy types and their specific capabilities.
        """.trimIndent()
    }
    
    /**
     * Creates ReAct-based multi-step execution planning prompt
     */
    override fun createPrompt(userMessage: String): Prompt {
        val messages: MutableList<Message> = mutableListOf()
        
        // Add ReAct system prompt
        messages.add(SystemMessage(getSystemPrompt()))
        
        // Request ReAct framework analysis of user message
        val analysisPrompt = """
            Analyze the following user message using the ReAct framework and divide & conquer approach:
            
            User Message: "$userMessage"
            
            Apply the ReAct process (THOUGHT → ACTION → OBSERVATION) systematically and provide your analysis in the specified JSON format. Focus on breaking down the query into manageable components and selecting the optimal strategy with detailed reasoning.
        """.trimIndent()
        
        messages.add(UserMessage(analysisPrompt))
        
        return Prompt(messages)
    }
    
    /**
     * ReAct execution planning may use tools for context analysis and validation
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry, popupToolRegistry, buildingToolRegistry), *mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.2)
            .build()
            
        return toolCallbackChatOptions
    }
} 