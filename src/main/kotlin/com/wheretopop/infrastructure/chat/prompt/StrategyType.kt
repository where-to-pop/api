package com.wheretopop.infrastructure.chat.prompt

/**
 * 챗봇 프롬프트 전략의 타입을 정의하는 enum 클래스
 * 각 전략 타입은 고유한 식별자를 가지며, 전략을 명시적으로 선택할 때 사용됩니다.
 */
enum class StrategyType(val id: String, val description: String) {
    /**
     * 채팅 제목을 생성하는 전략
     */
    TITLE_GENERATION("title_generation", "Generates a title for the chat conversation based on the user's first message"),
    
    /**
     * 지역 정보를 조회하는 전략
     */
    AREA_QUERY("area_query", "Provides information about areas, including congestion levels and population insights"),

    /**
     * 건물 정보를 조회하는 전략
     */
    BUILDING_QUERY("building_query", "Provides detailed information about buildings, including specifications, facilities, and approval details"),
    
    /**
     * 팝업 스토어 정보를 조회하는 전략
     */
    POPUP_QUERY("popup_query", "Provides information about popup stores and events, including details about location, duration, and themes"),
    
    /**
     * 적합한 전략을 선택하는 전략
     */
    STRATEGY_SELECTOR("strategy_selector", "Analyzes user message and suggests the most appropriate strategy to use");
    
    companion object {
        /**
         * ID로 전략 타입을 찾습니다.
         *
         * @param id 전략 타입 ID
         * @return 해당 ID를 가진 전략 타입, 없으면 null
         */
        fun findById(id: String): StrategyType? {
            return values().find { it.id == id }
        }
    }
} 