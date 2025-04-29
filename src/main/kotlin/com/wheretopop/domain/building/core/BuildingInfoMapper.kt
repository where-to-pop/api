package com.wheretopop.domain.building.core

/**
 * 도메인 객체에서 DTO로 변환하는 매퍼 클래스
 */
class BuildingInfoMapper {

    /**
     * Building 도메인 객체를 BuildingInfo.Main DTO로 변환
     */
    fun of(domain: Building): BuildingInfo.Main {
        return BuildingInfo.Main(
            id = domain.id.value,
            address = domain.address,
            location = BuildingInfo.LocationInfo(
                latitude = domain.location.latitude,
                longitude = domain.location.longitude
            ),
        )
    }

    /**
     * Building 도메인 객체 목록을 BuildingInfo.Main DTO 목록으로 변환
     */
    fun of(domains: List<Building>): List<BuildingInfo.Main> {
        return domains.map(::of)
    }

}