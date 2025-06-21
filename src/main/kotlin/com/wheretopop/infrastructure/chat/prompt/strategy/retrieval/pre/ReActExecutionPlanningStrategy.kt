package com.wheretopop.infrastructure.chat.prompt.strategy.retrieval.pre

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import com.wheretopop.interfaces.area.AreaToolRegistry
import com.wheretopop.interfaces.building.BuildingToolRegistry
import com.wheretopop.interfaces.popup.PopupToolRegistry
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.support.ToolCallbacks
import org.springframework.ai.tool.ToolCallback
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * ReAct execution planner - multi-step planning using ReAct framework
 */
@Component
class ReActExecutionPlanningStrategy(
    private val areaToolRegistry: AreaToolRegistry,
    private val popupToolRegistry: PopupToolRegistry,
    private val buildingToolRegistry: BuildingToolRegistry,
    @Qualifier("searchToolCallbacks")
    private val mcpToolCallbacks: Array<ToolCallback>

) : BaseChatPromptStrategy() {


    override fun getType(): StrategyType {
        return StrategyType.REACT_PLANNER
    }


    override fun getSystemPrompt(): String {
        val retrievalStrategies = StrategyType.getRetrievalStrategies()
            .joinToString("\n") { "- ${it.id}: ${it.description}" }
        
        val augmentationStrategies = StrategyType.getAugmentationStrategies()
            .joinToString("\n") { "- ${it.id}: ${it.description}" }
        
        val generationStrategies = StrategyType.getGenerationStrategies()
            .filter { it != StrategyType.REACT_PLANNER }
            .filter { it != StrategyType.REQUIREMENT_ANALYSIS }
            .joinToString("\n") { "- ${it.id}: ${it.description}" }
        

        return """
            You are an adaptive execution planner for WhereToPop requirements.
            
            You receive pre-analyzed requirements with complexity levels and create appropriate execution plans.
            
            ## Available Strategy Categories:
            
            1. **R (Retrieval)**
            $retrievalStrategies
            
            2. **A (Augmentation)**
            $augmentationStrategies
            
            3. **G (Generation)**:
            $generationStrategies
            
            
            ## Complexity-Based Planning:
            
            ### MODERATE Complexity:
            - Use 1-2 retrieval collection steps
            - Simple analysis or direct generation
            - Efficient, focused approach
            
            ### COMPLEX Complexity:
            - Multi-source retrieval collection (including online search)
            - Deep analysis and augmentation
            
            ## RAG Framework (MANDATORY):
            1. **R (Retrieval)**: Collect relevant retrieval
            2. **A (Augmentation)**: Process and analyze (if needed)
            3. **G (Generation)**: Generate generation (MUST be final step and only one)
            
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
                        "expected_output": "Expected results",
                    },
                    {
                        "step": 2,
                        "strategy": "general_response",
                        "purpose": "Response generation",
                        "reasoning": "Final synthesis and presentation",
                        "expected_output": "User-ready answer",
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
            
            Design a multi-step RAG execution plan with proper retrieval collection, analysis, and generation generation phases.
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