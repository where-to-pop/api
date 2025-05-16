package com.wheretopop.domain.area

/**
 * 도메인 객체에서 DTO로 변환하는 매퍼 클래스
 */
class AreaInfoMapper {

    /**
     * Area 도메인 객체를 AreaInfo.Main DTO로 변환
     */
    fun of(domain: Area): AreaInfo.Main {
        return AreaInfo.Main(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            location = AreaInfo.LocationInfo(
                latitude = domain.location.latitude,
                longitude = domain.location.longitude
            ),
        )
    }

    /**
     * Area 도메인 객체 목록을 AreaInfo.Main DTO 목록으로 변환
     */
    fun of(domains: List<Area>): List<AreaInfo.Main> {
        return domains.map(::of)
    }

    /**
     * Area 도메인 객체와 AreaInsight 도메인 객체를 AreaInfo.Detail DTO로 변환
     */
    fun of(domain: Area, areaInsight: AreaInfo.PopulationInsight?): AreaInfo.Detail {
        return AreaInfo.Detail(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            location = AreaInfo.LocationInfo(
                latitude = domain.location.latitude,
                longitude = domain.location.longitude
            ),
            populationInsight = areaInsight,
        )
    }
}