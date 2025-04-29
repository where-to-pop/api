package com.wheretopop.domain.building.core

/**
 * 도메인 계층 외부에 넘겨줄 Building 관련 DTO 클래스들
 * 도메인 로직이 누설되지 않도록 데이터만 전달
 * BuildingInfo는 data만 정의된 class로 사용, 변환의 책임은 mapper class를 활용
 */
class BuildingInfo {

    /**
     * Building 기본 정보를 담은 DTO
     */
    data class Main(
        val id: Long,
        val address: String,
        val location: LocationInfo,
    )
    
    /**
     * Location 정보를 담은 DTO
     */
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double
    )
}