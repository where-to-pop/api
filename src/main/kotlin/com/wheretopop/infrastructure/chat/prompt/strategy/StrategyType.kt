package com.wheretopop.infrastructure.chat.prompt.strategy

/**
 * Strategy execution types for better orchestration
 */
enum class StrategyExecutionType {
    PRE_RETRIEVAL,
    RETRIEVAL,
    AUGMENTATION,
    GENERATION
}

/**
 * Chatbot prompt strategy types definition enum class
 * Each strategy type has a unique identifier and execution type for better orchestration
 */
enum class StrategyType(
    val id: String, 
    val description: String,
    val executionType: StrategyExecutionType,
    val displayInfo: StrategyDisplayInfo
) {
    /**
     * Chat title generation strategy
     */
    TITLE_GENERATION(
        "title_generation", 
        "Generates a title for the chat conversation based on the user's first message",
        StrategyExecutionType.GENERATION,
        StrategyDisplayInfo("제목 생성", "제목을 생성하고 있어요", "제목을 생성했어요")
    ),
    
    // ============ DATA COLLECTION STRATEGIES ============
    /**
     * Area information query strategy
     */
    AREA_QUERY(
        "area_query", 
        "Collects comprehensive area information including congestion, demographics, and characteristics",
        StrategyExecutionType.RETRIEVAL,
        StrategyDisplayInfo("지역 정보", "지역 정보를 찾고 있어요", "지역 정보를 찾았어요")
    ),

    /**
     * Building information query strategy
     */
    BUILDING_QUERY(
        "building_query", 
        "Collects detailed building information including specifications, facilities, and approval details",
        StrategyExecutionType.RETRIEVAL,
        StrategyDisplayInfo( "건물 정보", "건물 정보를 조회하고 있어요", "건물 정보를 찾았어요")
    ),
    
    /**
     * Popup store information query strategy
     */
    POPUP_QUERY(
        "popup_query", 
        "Collects popup store and event information including cases, trends, and success patterns",
        StrategyExecutionType.RETRIEVAL,
        StrategyDisplayInfo( "팝업스토어", "팝업스토어 정보를 찾고 있어요", "팝업스토어 정보를 찾았어요")
    ),
    
    /**
     * Online search strategy using web search MCP tools
     */
    ONLINE_SEARCH(
        "online_search",
        "Performs comprehensive online search for real-time information",
        StrategyExecutionType.RETRIEVAL,
        StrategyDisplayInfo("온라인 검색", "최신 정보를 검색하고 있어요", "최신 정보를 찾았어요")
    ),
    
    /**
     * Trend information query strategy
     */
    TREND_QUERY(
        "trend_query",
        "Collects industry trends, consumer behavior patterns, and market insights using web search",
        StrategyExecutionType.RETRIEVAL,
        StrategyDisplayInfo("트렌드 정보", "트렌드 정보를 찾고 있어요", "트렌드 정보를 찾았어요")
    ),
    
    /**
     * Competitor information query strategy
     */
    COMPETITOR_QUERY(
        "competitor_query", 
        "Collects competitor information including pricing, operations, and market positioning using web search",
        StrategyExecutionType.RETRIEVAL,
        StrategyDisplayInfo("경쟁사 정보", "경쟁사 정보를 찾고 있어요", "경쟁사 정보를 찾았어요")
    ),
    
    // ============ DATA PROCESSING STRATEGIES ============
    /**
     * Data aggregation strategy
     */
    DATA_AGGREGATION(
        "data_aggregation", 
        "Aggregates and combines information from multiple retrieval sources",
        StrategyExecutionType.AUGMENTATION,
        StrategyDisplayInfo("정보 정리", "수집한 정보를 정리하고 있어요", "정보 정리가 완료되었어요")
    ),
    
    // ============ DECISION MAKING STRATEGIES ============
    /**
     * User requirement analysis strategy - 사용자 요구사항 분석 및 복잡도 평가
     */
    REQUIREMENT_ANALYSIS(
        "requirement_analysis",
        "Analyzes user requirements and conversation context to determine complexity level",
        StrategyExecutionType.PRE_RETRIEVAL,
        StrategyDisplayInfo("요구사항 분석", "질문을 분석하고 있어요", "요구사항을 파악했어요")
    ),
    
    /**
     * Location suitability assessment strategy
     */
    LOCATION_ASSESSMENT(
        "location_assessment", 
        "Assesses location suitability based on project requirements and constraints",
        StrategyExecutionType.PRE_RETRIEVAL,
        StrategyDisplayInfo( "입지 분석", "입지를 분석하고 있어요", "입지 분석이 완료되었어요")
    ),
    
    // ============ RESPONSE GENERATION STRATEGIES ============
    /**
     * Area scope definition generation strategy
     */
    AREA_SCOPE_RESPONSE(
        "area_scope_response", 
        "Generates responses for area scope definition queries with geographical boundaries",
        StrategyExecutionType.GENERATION,
        StrategyDisplayInfo( "지역 범위", "지역 범위를 정의하고 있어요", "지역 범위를 정의했어요")
    ),
    
    /**
     * Location recommendation generation strategy
     */
    LOCATION_RECOMMENDATION_RESPONSE(
        "location_recommendation_response", 
        "Generates location and building recommendation responses with detailed rationale",
        StrategyExecutionType.GENERATION,
        StrategyDisplayInfo( "위치 추천", "맞춤 위치를 추천하고 있어요", "위치 추천을 준비했어요")
    ),
    
    /**
     * Case study analysis generation strategy
     */
    CASE_STUDY_RESPONSE(
        "case_study_response", 
        "Generates case study analysis responses with insights and patterns",
        StrategyExecutionType.GENERATION,
        StrategyDisplayInfo( "사례 분석", "사례를 분석하고 있어요", "사례 분석이 완료되었어요")
    ),
    
    /**
     * Price estimation generation strategy
     */
    PRICE_ESTIMATION_RESPONSE(
        "price_estimation_response",
        "Generates comprehensive price estimation for popup store setup including rent, facilities, and operational costs",
        StrategyExecutionType.GENERATION,
        StrategyDisplayInfo("가격 추정", "비용을 계산하고 있어요", "가격 추정이 완료되었어요")
    ),
    
    /**
     * Popup store planning generation strategy  
     */
    PLANNING_RESPONSE(
        "planning_response",
        "Generates comprehensive popup store planning proposals including concept, timeline, and execution strategy",
        StrategyExecutionType.GENERATION,
        StrategyDisplayInfo("기획 제안", "기획안을 작성하고 있어요", "기획 제안이 완료되었어요")
    ),

    /**
     * ReAct execution planner
     */
    REACT_PLANNER(
        "react_planner",
        "Creates comprehensive multi-step execution plans using ReAct framework",
        StrategyExecutionType.PRE_RETRIEVAL,
        StrategyDisplayInfo( "계획 수립", "실행 계획을 세우고 있어요", "실행 계획을 완료했어요")
    ),
    
    /**
     * General conversation generation strategy - IMPORTANT: Used as fallback
     */
    GENERAL_RESPONSE(
        "general_response", 
        "Generates general conversation responses for basic queries and interactions - used as fallback",
        StrategyExecutionType.GENERATION,
        StrategyDisplayInfo( "답변 생성", "답변을 작성하고 있어요", "답변이 준비되었어요")
    );
    
    companion object {
        /**
         * Finds strategy type by ID
         */
        fun findById(id: String): StrategyType? {
            return values().find { it.id == id }
        }
        
        /**
         * Gets strategies by execution type
         */
        fun getByExecutionType(executionType: StrategyExecutionType): List<StrategyType> {
            return values().filter { it.executionType == executionType }
        }
        
        /**
         * Gets retrieval collection strategies
         */
        fun getRetrievalStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.RETRIEVAL)
        }
        
        /**
         * Gets retrieval processing strategies
         */
        fun getAugmentationStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.AUGMENTATION)
        }
        
        /**
         * Gets augmentation making strategies
         */
        fun getPreRetrievalStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.PRE_RETRIEVAL)
        }
        
        /**
         * Gets generation generation strategies
         */
        fun getGenerationStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.GENERATION)
        }

        /**
         * UX 친화적인 메시지 생성 유틸리티
         */
        fun buildExecutingMessage(strategyId: String, purpose: String? = null): String {
            val strategy = findById(strategyId)
            return if (strategy != null) {
                strategy.displayInfo.executingMessage
            } else {
                "${extractUserFriendlyPurpose(purpose ?: "작업")}하고 있어요"
            }
        }
        
        fun buildCompletedMessage(strategyId: String, purpose: String? = null): String {
            val strategy = findById(strategyId)
            return if (strategy != null) {
                strategy.displayInfo.completedMessage
            } else {
                "${extractUserFriendlyPurpose(purpose ?: "작업")}했어요"
            }
        }
        
        fun buildProgressMessage(strategyId: String, currentStep: Int, totalSteps: Int): String {
            val strategy = findById(strategyId)
            val stepInfo = if (totalSteps > 1) " (${currentStep}/${totalSteps})" else ""
            
            return if (strategy != null) {
                "${strategy.displayInfo.categoryName}${stepInfo}"
            } else {
                "작업 진행 중${stepInfo}"
            }
        }
        
        fun buildErrorMessage(strategyId: String?, error: String): String {
            val strategy = strategyId?.let { findById(it) }
            val friendlyError = when {
                error.contains("timeout", true) -> "시간이 조금 오래 걸리고 있어요"
                error.contains("network", true) -> "네트워크 연결에 문제가 있어요"
                error.contains("not found", true) -> "정보를 찾지 못했어요"
                error.contains("connection", true) -> "연결에 문제가 있어요"
                else -> "일시적인 문제가 발생했어요"
            }
            
            return friendlyError
        }
        
        fun buildPhaseMessage(phase: com.wheretopop.infrastructure.chat.prompt.ExecutionPhase): String {
            return when (phase) {
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.PLANNING -> "질문을 분석하고 있어요"
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.STEP_EXECUTING -> "작업을 진행하고 있어요"
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.STEP_COMPLETED -> "단계가 완료되었어요"
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.STEP_FAILED -> "문제가 발생했어요"
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.AGGREGATING -> "정보를 정리하고 있어요"
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.COMPLETED -> "모든 정보를 찾았어요!"
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.FAILED -> "죄송해요, 문제가 발생했어요"
                com.wheretopop.infrastructure.chat.prompt.ExecutionPhase.CLOSED -> "연결이 닫혔어요."
            }
        }
        
        private fun extractUserFriendlyPurpose(purpose: String): String {
            return when {
                purpose.contains("지역", true) || purpose.contains("area", true) -> "지역 정보를 찾"
                purpose.contains("건물", true) || purpose.contains("building", true) -> "건물 정보를 찾"
                purpose.contains("팝업", true) || purpose.contains("popup", true) -> "팝업스토어 정보를 찾"
                purpose.contains("검색", true) || purpose.contains("search", true) -> "정보를 검색"
                purpose.contains("분석", true) || purpose.contains("analysis", true) -> "정보를 분석"
                purpose.contains("비교", true) || purpose.contains("comparison", true) -> "옵션들을 비교"
                purpose.contains("추천", true) || purpose.contains("recommend", true) -> "추천을 준비"
                purpose.contains("가격", true) || purpose.contains("비용", true) || purpose.contains("price", true) -> "가격을 계산"
                purpose.contains("기획", true) || purpose.contains("계획", true) || purpose.contains("planning", true) -> "기획안을 작성"
                purpose.contains("트렌드", true) || purpose.contains("trend", true) -> "트렌드를 조사"
                purpose.contains("경쟁", true) || purpose.contains("competitor", true) -> "경쟁사를 조사"
                purpose.contains("Step objective", true) -> "정보를 처리"
                else -> purpose.take(20)
            }
        }
    }
}

/**
 * 각 전략의 사용자 친화적인 표시 정보
 */
data class StrategyDisplayInfo(
    val categoryName: String,   // 카테고리 명칭
    val executingMessage: String, // 실행 중 메시지
    val completedMessage: String  // 완료 메시지
) 