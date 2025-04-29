package com.wheretopop.domain.building.register

import com.wheretopop.domain.building.core.BuildingId


/**
 * Building Register 검색을 위한 기준 클래스
 */
sealed class BuildingRegisterCriteria {
    /**
     * 기본 검색 조건 (지역, 키워드 등)
     */
    data class SearchBuildingRegisterCriteria(
        val buildingId: BuildingId? = null,
        val address: String? = null,
        val limit: Int = 20,
        val offset: Int = 0
    ) : BuildingRegisterCriteria()
} 