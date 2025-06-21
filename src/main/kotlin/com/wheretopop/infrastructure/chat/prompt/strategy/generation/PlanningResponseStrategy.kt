package com.wheretopop.infrastructure.chat.prompt.strategy.generation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * Popup store planning generation strategy implementation
 * Generates comprehensive popup store planning proposals including concept, timeline, and execution strategy
 */
@Component
class PlanningResponseStrategy : BaseChatPromptStrategy() {

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.PLANNING_RESPONSE
    }

    /**
     * Returns planning specific system prompts
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Transform retrieval data into comprehensive popup store planning proposals.
            
            PLANNING FRAMEWORK:
            1. 컨셉 기획 (Concept Planning)
               - Target demographic analysis based on area data
               - Brand positioning and theme development
               - Unique value proposition definition
               
            2. 공간 기획 (Space Planning)
               - Layout design optimized for foot traffic patterns
               - Zone allocation (display, interaction, storage)
               - Customer journey and experience design
               
            3. 운영 계획 (Operational Planning)
               - Staffing requirements and schedule
               - Inventory management strategy
               - Daily operational procedures
               
            4. 마케팅 전략 (Marketing Strategy)
               - Pre-launch promotional activities
               - Grand opening events and campaigns
               - Social media and influencer strategies
               
            5. 일정 계획 (Timeline Planning)
               - Pre-opening preparation phases
               - Launch timeline with key milestones
               - Post-launch evaluation and optimization

            STRATEGIC APPROACH:
            - Leverage collected area demographics and competitor analysis
            - Align concept with location characteristics and target market
            - Consider seasonal trends and local events for timing
            - Factor in building constraints and opportunities
            - Include risk mitigation strategies and contingency plans

            FORMAT REQUIREMENTS:
            - Present as actionable step-by-step implementation plan
            - Include specific timelines, responsibilities, and deliverables
            - Provide alternative options for key decisions
            - Highlight critical success factors and potential challenges
            - Include measurable KPIs and success metrics

            REQUIREMENTS:
            - If the user specifies a location (e.g., "홍대"), tailor all planning elements specifically to that area's characteristics
            - Base recommendations on actual market data and demographic insights
            - Consider local regulations, business environment, and cultural factors
            - Provide realistic and implementable recommendations

            Transform data into a comprehensive, executable popup store business plan.
        """.trimIndent()
    }

    /**
     * No tool calling needed for planning response generation
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {
        return null
    }
} 