package com.wheretopop.infrastructure.chat.prompt.strategy

/**
 * Strategy execution types for better orchestration
 */
enum class StrategyExecutionType {
    DATA_COLLECTION,    // 데이터 수집 단계
    DATA_PROCESSING,    // 데이터 처리/분석 단계  
    DECISION_MAKING,    // 의사결정/추론 단계
    RESPONSE_GENERATION // 최종 응답 생성 단계
}

/**
 * Chatbot prompt strategy types definition enum class
 * Each strategy type has a unique identifier and execution type for better orchestration
 */
enum class StrategyType(
    val id: String, 
    val description: String,
    val executionType: StrategyExecutionType
) {
    /**
     * Chat title generation strategy
     */
    TITLE_GENERATION(
        "title_generation", 
        "Generates a title for the chat conversation based on the user's first message",
        StrategyExecutionType.RESPONSE_GENERATION
    ),
    
    // ============ DATA COLLECTION STRATEGIES ============
    /**
     * Area information query strategy
     */
    AREA_QUERY(
        "area_query", 
        "Collects comprehensive area information including congestion, demographics, and characteristics",
        StrategyExecutionType.DATA_COLLECTION
    ),

    /**
     * Building information query strategy
     */
    BUILDING_QUERY(
        "building_query", 
        "Collects detailed building information including specifications, facilities, and approval details",
        StrategyExecutionType.DATA_COLLECTION
    ),
    
    /**
     * Popup store information query strategy
     */
    POPUP_QUERY(
        "popup_query", 
        "Collects popup store and event information including cases, trends, and success patterns",
        StrategyExecutionType.DATA_COLLECTION
    ),
    
    /**
     * Online search strategy using web search MCP tools
     */
    ONLINE_SEARCH(
        "online_search",
        "Performs comprehensive online search for real-time information",
        StrategyExecutionType.DATA_COLLECTION
    ),
    
    // ============ DATA PROCESSING STRATEGIES ============
    /**
     * Data aggregation strategy
     */
    DATA_AGGREGATION(
        "data_aggregation", 
        "Aggregates and combines information from multiple data sources",
        StrategyExecutionType.DATA_PROCESSING
    ),
    
    /**
     * Data filtering and selection strategy
     */
    DATA_FILTERING(
        "data_filtering", 
        "Filters and selects relevant data based on user query requirements",
        StrategyExecutionType.DATA_PROCESSING
    ),
    
    // ============ DECISION MAKING STRATEGIES ============
    /**
     * Location suitability assessment strategy
     */
    LOCATION_ASSESSMENT(
        "location_assessment", 
        "Assesses location suitability based on project requirements and constraints",
        StrategyExecutionType.DECISION_MAKING
    ),
    
    // ============ RESPONSE GENERATION STRATEGIES ============
    /**
     * Area scope definition response strategy
     */
    AREA_SCOPE_RESPONSE(
        "area_scope_response", 
        "Generates responses for area scope definition queries with geographical boundaries",
        StrategyExecutionType.RESPONSE_GENERATION
    ),
    
    /**
     * Location recommendation response strategy
     */
    LOCATION_RECOMMENDATION_RESPONSE(
        "location_recommendation_response", 
        "Generates location and building recommendation responses with detailed rationale",
        StrategyExecutionType.RESPONSE_GENERATION
    ),
    
    /**
     * Case study analysis response strategy
     */
    CASE_STUDY_RESPONSE(
        "case_study_response", 
        "Generates case study analysis responses with insights and patterns",
        StrategyExecutionType.RESPONSE_GENERATION
    ),

    /**
     * ReAct execution planner
     */
    REACT_PLANNER(
        "react_planner",
        "Creates comprehensive multi-step execution plans using ReAct framework",
        StrategyExecutionType.DECISION_MAKING
    ),
    
    /**
     * General conversation response strategy - IMPORTANT: Used as fallback
     */
    GENERAL_RESPONSE(
        "general_response", 
        "Generates general conversation responses for basic queries and interactions - used as fallback",
        StrategyExecutionType.RESPONSE_GENERATION
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
         * Gets data collection strategies
         */
        fun getDataCollectionStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.DATA_COLLECTION)
        }
        
        /**
         * Gets data processing strategies
         */
        fun getDataProcessingStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.DATA_PROCESSING)
        }
        
        /**
         * Gets decision making strategies
         */
        fun getDecisionMakingStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.DECISION_MAKING)
        }
        
        /**
         * Gets response generation strategies
         */
        fun getResponseGenerationStrategies(): List<StrategyType> {
            return getByExecutionType(StrategyExecutionType.RESPONSE_GENERATION)
        }
    }
} 