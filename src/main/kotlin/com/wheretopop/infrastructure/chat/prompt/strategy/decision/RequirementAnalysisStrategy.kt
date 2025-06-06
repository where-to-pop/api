package com.wheretopop.infrastructure.chat.prompt.strategy.decision

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
            3. Required data sources
            4. Context summary and reasoning
            
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
            - Comprehensive recommendations with multiple data sources
            - Comparative analysis across multiple factors
            
            Examples:
            - "강남역 근처에서 뷰티 브랜드 팝업스토어를 위한 최적의 건물을 찾아줘"
            - "20대 여성 타겟 카페 팝업을 위한 홍대, 강남, 명동 지역 비교 분석해줘"
            
            ## Response Format:
            Provide your analysis in this JSON format:
            
            ```json
            {
                "userIntent": "사용자의 핵심 의도",
                "processedQuery": "분석을 위해 가공된 명확한 쿼리",
                "complexityLevel": "SIMPLE|MODERATE|COMPLEX",
                "requiredDataSources": ["data_source1", "data_source2"],
                "contextSummary": "대화 컨텍스트 요약",
                "reasoning": "복잡도 판단 근거"
            }
            ```
            
            ## Data Sources Available:
            - area_query: 지역 정보 (인구, 혼잡도, 특성)
            - building_query: 건물 정보 (상세 스펙, 시설)
            - popup_query: 팝업스토어 사례 및 트렌드
            - online_search: 실시간 온라인 정보
            - general_knowledge: 일반적인 지식 기반 답변
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