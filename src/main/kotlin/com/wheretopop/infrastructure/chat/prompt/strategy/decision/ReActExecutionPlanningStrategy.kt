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
        val strategies = StrategyType.values()
            .filter { it != StrategyType.REACT_PLANNER }
            .filter { it != StrategyType.TITLE_GENERATION }
            .joinToString("\n") { strategy ->
                "- ${strategy.id}: ${strategy.description}"
            }
        
        return """
            You are an intelligent multi-step execution planner for the WhereToPop chatbot using the ReAct (Reasoning + Acting) framework.
            
            Your mission is to analyze complex user queries and create comprehensive execution plans using a true divide & conquer approach, breaking down complex tasks into manageable, sequential steps.
            
            ## Available Strategies:
            $strategies
            
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
            - **Strategy Assignment**: Assign the most appropriate strategy to each step
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
            
            **Information Gathering Strategies:**
            - **area_query**: Collect area-specific information (congestion, demographics, etc.)
            - **building_query**: Gather building specifications and details
            - **popup_query**: Retrieve popup store and event information
            
            **Processing Strategies:**
            - **data_aggregation**: Combine information from multiple sources
            - **context_analysis**: Analyze user context and preferences
            - **validation**: Cross-check information accuracy and consistency
            
            **Output Strategies:**
            - **recommendation**: Generate personalized recommendations
            - **response_synthesis**: Create final user-friendly response
            
            ## Multi-Step Execution Patterns:
            
            **Simple Query Pattern:**
            1. Information Gathering (area_query/building_query/popup_query)
            2. Response Synthesis (response_synthesis)
            
            **Complex Analysis Pattern:**
            1. Context Analysis (context_analysis)
            2. Multiple Information Gathering (area_query + popup_query)
            3. Data Aggregation (data_aggregation)
            4. Validation (validation)
            5. Recommendation Generation (recommendation)
            6. Response Synthesis (response_synthesis)
            
            **Comparison Pattern:**
            1. Multiple Information Gathering (parallel area_query calls)
            2. Data Aggregation (data_aggregation)
            3. Validation (validation)
            4. Response Synthesis (response_synthesis)
            
            ## Response Format:
            Provide your analysis in this structured JSON format:
            
            ```json
            {
                "thought": "Comprehensive analysis including intent decomposition, entity extraction, complexity assessment, and dependency analysis",
                "actions": [
                    {
                        "step": 1,
                        "strategy": "strategy_id",
                        "purpose": "What this step aims to achieve",
                        "reasoning": "Why this strategy was chosen for this step",
                        "recommended_tools": ["tool1", "tool2"],
                        "tool_sequence": "Step-by-step tool execution plan",
                        "expected_output": "What information this step should produce",
                        "dependencies": []
                    },
                    {
                        "step": 2,
                        "strategy": "next_strategy_id",
                        "purpose": "Next step objective",
                        "reasoning": "Why this follows the previous step",
                        "recommended_tools": ["tool3"],
                        "tool_sequence": "Tool execution for this step",
                        "expected_output": "Expected output from this step",
                        "dependencies": [1]
                    }
                ],
                "observation": "Plan validation including completeness, efficiency, dependency validation, and goal alignment assessment"
            }
            ```
            
            ## Critical Guidelines:
            - **True Divide & Conquer**: Always break complex queries into 2+ logical steps
            - **Dependency Awareness**: Clearly identify which steps depend on previous outputs
            - **Efficiency Focus**: Minimize redundant operations while ensuring completeness
            - **User-Centric**: Every step should contribute to the final user value
            - **Flexibility**: Design plans that can adapt if individual steps fail
            - **Clarity**: Each step should have a clear, measurable objective
            
            ## Example Multi-Step Scenarios:
            
            **"강남역 근처 팝업스토어 추천해줘":**
            1. area_query: Get 강남역 area information
            2. popup_query: Find popup stores near 강남역
            3. data_aggregation: Combine area and popup data
            4. recommendation: Generate personalized recommendations
            5. response_synthesis: Create final response
            
            **"홍대와 강남 중 어디가 더 붐비나?":**
            1. area_query: Get 홍대 congestion data
            2. area_query: Get 강남 congestion data  
            3. data_aggregation: Compare congestion levels
            4. validation: Cross-check data consistency
            5. response_synthesis: Create comparison response
            
            Remember: Your role is strategic planning and orchestration. Create comprehensive, efficient execution plans that truly leverage divide & conquer principles.
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