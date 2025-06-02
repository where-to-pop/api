package com.wheretopop.infrastructure.chat.prompt

import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * 실행 계획 캐시 관리를 담당하는 클래스
 */
@Component
class ExecutionCacheManager {
    private val logger = KotlinLogging.logger {}
    private val executionPlanCache = ConcurrentHashMap<String, ReActResponse>()
    
    /**
     * 유스케이스별 최적화된 캐시 키를 생성합니다.
     */
    fun generateCacheKey(userMessage: String): String {
        return when {
            // 유스케이스 1: 사례 기반 지역 범위 정의
            userMessage.contains("지역") && (userMessage.contains("범위") || userMessage.contains("정의")) -> 
                "area_scope_definition"
            
            // 유스케이스 2: 프로젝트 요구사항 기반 지역 분석 및 추천
            userMessage.contains("분석") && userMessage.contains("추천") -> 
                "location_analysis_recommendation"
            
            // 유스케이스 3: 특정 지역의 건물 추천
            userMessage.contains("건물") && userMessage.contains("추천") -> 
                "building_recommendation"
            
            // 유스케이스 4: 브랜드 특성 기반 팝업 사례 분석
            userMessage.contains("팝업") && (userMessage.contains("사례") || userMessage.contains("분석")) -> 
                "popup_case_study"
            
            // 유스케이스 5: 미래 예측 시나리오
            userMessage.contains("미래") || userMessage.contains("예측") || userMessage.contains("전망") -> 
                "future_prediction"
            
            // 기존 패턴들
            userMessage.contains("추천") && userMessage.contains("팝업") -> "popup_recommendation"
            userMessage.contains("비교") || userMessage.contains("어디가") -> "area_comparison"
            userMessage.contains("혼잡") || userMessage.contains("붐비") -> "congestion_query"
            userMessage.contains("건물") -> "building_query"
            userMessage.contains("지역") || userMessage.contains("역") -> "area_query"
            else -> "general_query"
        }
    }
    
    /**
     * 캐시에서 실행 계획을 조회합니다.
     */
    fun getExecutionPlan(cacheKey: String): ReActResponse? {
        val cached = executionPlanCache[cacheKey]
        if (cached != null) {
            logger.info("Cache hit for key: $cacheKey")
        }
        return cached
    }
    
    /**
     * 실행 계획을 캐시에 저장합니다.
     */
    fun putExecutionPlan(cacheKey: String, executionPlan: ReActResponse) {
        executionPlanCache[cacheKey] = executionPlan
        logger.info("Cached execution plan for key: $cacheKey with ${executionPlan.actions.size} steps")
    }
    
    /**
     * 캐시를 클리어합니다.
     */
    fun clearCache() {
        val size = executionPlanCache.size
        executionPlanCache.clear()
        logger.info("Cleared execution plan cache ($size entries)")
    }
    
    /**
     * 캐시 통계를 반환합니다.
     */
    fun getCacheStats(): Map<String, Any> {
        return mapOf(
            "size" to executionPlanCache.size,
            "keys" to executionPlanCache.keys.toList()
        )
    }
} 