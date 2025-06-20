package com.wheretopop.infrastructure.chat.prompt.strategy.augmentation

import com.wheretopop.infrastructure.chat.prompt.strategy.BaseChatPromptStrategy
import com.wheretopop.infrastructure.chat.prompt.strategy.StrategyType
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.stereotype.Component

/**
 * 사용자 요구사항 분석 및 복잡도 평가 Strategy
 */
@Component
class RequirementAnalysisStrategy : BaseChatPromptStrategy() {

    override fun getType(): StrategyType {
        return StrategyType.REQUIREMENT_ANALYSIS
    }

    override fun getSystemPrompt(): String {
        return """
            You are a requirement analysis specialist for WhereToPop platform.
            
            Your role is to analyze user requirements and conversation context to determine:
            1. User intent and processed query
            2. Complexity level (SIMPLE/MODERATE/COMPLEX)
            3. Context summary and reasoning
            
            ## Complexity Level Guidelines:
            
            ### SIMPLE (단순)
            - ONLY basic greetings and platform introduction questions
            - No business-related information requests
            - No location, building, or popup store questions
            - Must be answerable with simple platform introduction
            
            Examples:
            - "안녕?"
            - "안녕하세요"
            - "넌 누구야?"
            - "당신은 누구인가요?"
            - "여기는 뭐하는 곳이야?"
            - "이 서비스는 뭐야?"
            
            ### MODERATE (보통)
            - General business questions about popup stores, areas, or buildings
            - Basic information requests and explanations
            - Simple location or area questions
            - Basic comparison or recommendation needs
            
            Examples:
            - "팝업스토어란 뭐야?"
            - "20대 여성을 타겟으로 하는 팝업스토어에 대해 알려줘"
            - "팝업스토어 운영할 때 고려사항이 뭐야?"
            - "강남에서 팝업스토어 하면 어떨까?"
            - "홍대 근처 건물 정보 알려줘"
            - "명동과 강남 중 어디가 나을까?"
            
            ### COMPLEX (복잡)
            - Multi-criteria analysis requirements
            - Specific building or detailed location analysis
            - Comprehensive recommendations with multiple retrieval sources
            - Comparative analysis across multiple factors
            
            Examples:
            - "강남역 근처에서 뷰티 브랜드 팝업스토어를 위한 최적의 건물을 찾아줘"
            - "20대 여성 타겟 카페 팝업을 위한 홍대, 강남, 명동 지역 비교 분석해줘"
            
            ## Response Format:
            Provide your analysis in this JSON format:
            
            ```json
            {
                "userIntent": "현재 팝업스토어 시장에서 인기 있는 컨셉을 파악하여 프로젝트 기획에 참고하려는 것",
                "processedQuery": "최근 팝업스토어 시장에서 20~30대를 주요 타깃으로 하는 대형 브랜드 중심의 브랜딩 목적 팝업스토어 중 가장 효과적이었던 컨셉 유형을 분석해줘",
                "complexityLevel": "MODERATE",
                "contextSummary": "사용자는 '네이버 세미나 팝업'이라는 브랜딩 목적의 MEDIA 카테고리 팝업스토어를 기획 중이며, 주요 타깃은 20~30대이고, 팝업 기간은 약 2개월(2025-05-26~2025-07-29)이며 대형 브랜드로 분류됨. 최근 대화에서는 성수동의 인스턴트펑크 팝업스토어, 압구정의 비자레이매지네이션 팝업스토어 제공됨. 유저는 성수동과 압구정을 중심으로 탐색을 했음.",
                "reasoning": "단순한 인기 순위 응답을 넘어서, 사용자 프로젝트 맥락(브랜드 규모, 기간, 타깃 연령층, 카테고리)에 적합한 컨셉을 도출하려면 시장 트렌드와 사례 간 비교가 필요하며, 중간 수준의 맥락 보강 및 해석이 요구됨"
            }
            ```
            
        """.trimIndent()
    }

    override fun createPrompt(userMessage: String): Prompt {
        val messages: MutableList<Message> = mutableListOf()
        
        messages.add(SystemMessage(getSystemPrompt()))
        
        val analysisPrompt = """
            Analyze the following user request and conversation context:
            
            User Message: "$userMessage"
            
            Determine the complexity level and required approach for this request.
        """.trimIndent()
        
        messages.add(UserMessage(analysisPrompt))
        
        return Prompt(messages)
    }

    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        return ToolCallingChatOptions.builder()
            .temperature(0.1) // 일관된 분석을 위해 낮은 온도
            .build()
    }
} 