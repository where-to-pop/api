package com.wheretopop.domain.area

import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.Gender


/**
 * Area 검색을 위한 기준 클래스
 */
sealed class AreaCriteria {
    /**
     * 기본 검색 조건 (지역, 키워드 등)
     */
    data class SearchAreaCriteria(
        val keyword: String? = null, 
        val regionId: Long? = null, 
        val latitude: Double? = null, 
        val longitude: Double? = null,
        val radius: Double? = null,  // 반경 (km)
        val limit: Int = 20,
        val offset: Int = 0
    ) : AreaCriteria()
    
    /**
     * 인구 통계 기반 검색 조건
     */
    data class DemographicCriteria(
        val minPopulation: Int? = null,
        val ageRange: String? = null,
        val genderRatio: Double? = null,
        val baseSearchCriteria: SearchAreaCriteria = SearchAreaCriteria()
    ) : AreaCriteria()
    
    /**
     * 상업 정보 기반 검색 조건
     */
    data class CommercialCriteria(
        val minStoreCount: Int? = null,
        val storeCategory: String? = null,
        val maxRent: Long? = null,
        val baseSearchCriteria: SearchAreaCriteria = SearchAreaCriteria()
    ) : AreaCriteria()
} 