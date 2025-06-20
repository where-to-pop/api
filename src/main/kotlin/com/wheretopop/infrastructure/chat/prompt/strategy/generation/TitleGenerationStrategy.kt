package com.wheretopop.infrastructure.chat.prompt.strategy.generation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * Chat title generation strategy implementation
 * Generates a title for the chat conversation based on the user's first message
 */
@Component
class TitleGenerationStrategy : BaseChatPromptStrategy() {

    /**
     * Returns the strategy type
     */
    override fun getType(): StrategyType {
        return StrategyType.TITLE_GENERATION
    }

    /**
     * Returns title generation specific system prompt
     */
    override fun getAdditionalSystemPrompt(): String {
        return """
            Generate ONLY a short Korean title. MAXIMUM 6 words. NO explanations.
            
            STRICT FORMAT: <title>[제목]</title>
            
            EXAMPLES:
            User: "강남역 근처에 팝업스토어 열기 좋은 곳이 어디에요?"
            <title>강남역 팝업스토어 위치</title>
            
            User: "홍대 상권 분석해주세요"
            <title>홍대 상권 분석</title>
            
            User: "건대입구 어디에 있니?"
            <title>건대입구 위치 문의</title>
            
            User: "건물 임대 조건이 어떻게 되나요?"
            <title>건물 임대 조건</title>
            
            User: "이 지역 브랜드 팝업 성공 사례 있나요?"
            <title>브랜드 팝업 사례</title>
            
            RULES:
            - MAXIMUM 6 Korean words
            - Use <title></title> tags
            - NO explanations outside tags
            - NO long sentences
            - Nouns only
            
            OUTPUT EXACTLY: <title>[제목]</title>
        """.trimIndent()
    }

    /**
     * Title generation with strict token limits
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions? {
        return ToolCallingChatOptions.builder()
            .maxTokens(50) // 최대 50토큰으로 제한
            .temperature(0.1) // 창의성 최소화
            .build()
    }
} 