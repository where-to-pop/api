package com.wheretopop.domain.building.register


/**
 * Building Register 검색을 위한 기준 클래스
 */
sealed class BuildingRegisterCriteria {
    /**
     * 기본 검색 조건 (지역, 키워드 등)
     */
    data class SearchBuildingRegisterCriteria(
        val buildingId: BuildingRegisterId? = null,
        val address: String? = null,
        val limit: Int = 20,
        val offset: Int = 0
    ) : BuildingRegisterCriteria()
} 