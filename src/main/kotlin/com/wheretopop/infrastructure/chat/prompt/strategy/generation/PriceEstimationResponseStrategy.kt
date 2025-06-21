package com.wheretopop.infrastructure.chat.prompt.strategy.generation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * Price estimation generation strategy implementation
 * Generates comprehensive price estimation for popup store setup including rent, facilities, and operational costs
 */
@Component
class PriceEstimationResponseStrategy : BaseChatPromptStrategy() {

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.PRICE_ESTIMATION_RESPONSE
    }

    /**
     * Returns price estimation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Transform retrieval data into comprehensive price estimation for popup store setup.
            
            COST ANALYSIS CATEGORIES:
            1. 임대료 (Rent): Monthly rent, deposit, management fees
            2. 인테리어/시설 (Interior/Facilities): Design, construction, equipment costs
            3. 운영비 (Operational): Utilities, staffing, marketing, insurance
            4. 허가/인허가 (Permits): Legal requirements, registration fees
            5. 기타 비용 (Other): Unexpected costs, contingency funds

            ESTIMATION APPROACH:
            - Use collected building and area data to provide realistic price ranges
            - Factor in location premium, building grade, and market conditions
            - Provide cost breakdowns by category with clear explanations
            - Include both initial setup costs and monthly recurring costs
            - Consider seasonal variations and market trends

            FORMAT REQUIREMENTS:
            - Present costs in clear, structured format with KRW amounts
            - Provide both minimum and maximum estimates for each category
            - Include percentage breakdowns of total costs
            - Explain cost drivers and variables that affect pricing
            - Suggest cost optimization strategies where applicable

            REQUIREMENTS:
            - If the user specifies a location (e.g., "홍대"), focus cost analysis on that specific area. Do not include irrelevant locations
            - Base estimates on actual market data from the retrieved information
            - Clearly state assumptions and data sources used in calculations
            - Highlight factors that could significantly impact final costs

            Transform data into actionable cost planning information for popup store budgeting.
        """.trimIndent()
    }

    /**
     * No tool calling needed for price estimation response generation
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {
        return null
    }
} 