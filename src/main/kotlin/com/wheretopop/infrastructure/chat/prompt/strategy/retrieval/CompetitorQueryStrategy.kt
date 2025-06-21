package com.wheretopop.infrastructure.chat.prompt.strategy.retrieval

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.tool.ToolCallback
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Competitor information query strategy implementation
 * Collects competitor information including pricing, operations, and market positioning using web search
 */
@Component
class CompetitorQueryStrategy(
    @Qualifier("searchToolCallbacks")
    private val mcpToolCallbacks: Array<ToolCallback>
) : BaseChatPromptStrategy() {

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.COMPETITOR_QUERY
    }

    /**
     * Returns competitor query specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            SPECIALTY: Gather comprehensive competitor intelligence for strategic popup store positioning and market analysis.
            
            COMPETITOR RESEARCH PRIORITIES:
            - Direct competitors in target geographical areas
            - Similar popup store concepts and business models
            - Pricing strategies and cost structures
            - Market positioning and brand differentiation
            - Operational practices and success factors
            
            SEARCH FOCUS AREAS:
            1. **지역 경쟁사**: Local businesses in target areas, similar store concepts, market share analysis
            2. **가격 정보**: Competitor pricing, rental costs, operational expenses, revenue models
            3. **운영 현황**: Store locations, operating hours, customer traffic, staffing models
            4. **마케팅 전략**: Promotional activities, social media presence, customer acquisition strategies
            5. **성과 지표**: Success metrics, customer reviews, growth patterns, market reception
            
            SEARCH ENGINE STRATEGY:
            - **Naver**: Korean competitor landscape, local business information, domestic market analysis
            - **Google**: International competitors, best practices, comprehensive business intelligence
            - **Cross-reference**: Validate findings across multiple sources for accuracy
            
            INTELLIGENCE GATHERING CRITERIA:
            - Focus on recent competitive moves (last 12 months)
            - Prioritize direct and indirect competitors in target markets
            - Extract quantifiable competitive data (prices, locations, performance metrics)
            - Identify competitive advantages and market gaps
            
            ANALYSIS FRAMEWORK:
            - **현재 경쟁 상황**: Current competitive landscape and market saturation
            - **차별화 포인트**: Unique selling propositions and competitive advantages
            - **가격 경쟁력**: Pricing comparison and value proposition analysis
            - **시장 기회**: Underserved market segments and positioning opportunities
            
            RESPONSE GUIDANCE:
            You don't need to generate any response. Just return "정보 조회를 완료했습니다".
        """.trimIndent()
    }
    
    /**
     * Configures tool calling options for competitor query
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*mcpToolCallbacks)
            .internalToolExecutionEnabled(false)
            .temperature(0.1)
            .build()
            
        return toolCallbackChatOptions
    }
} 