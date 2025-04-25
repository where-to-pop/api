package com.wheretopop.domain.area

/**
 * 도메인 계층 외부에 넘겨줄 Area 관련 DTO 클래스들
 * 도메인 로직이 누설되지 않도록 데이터만 전달
 * AreaInfo는 data만 정의된 class로 사용, 변환의 책임은 mapper class를 활용
 */
class AreaInfo {

    /**
     * Area 기본 정보를 담은 DTO
     */
    data class Main(
        val id: Long,
        val name: String,
        val description: String?,
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