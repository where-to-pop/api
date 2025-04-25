package com.wheretopop.interfaces.area

import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaInfo
import org.springframework.stereotype.Component

/**
 * 인터페이스 레이어와 도메인 레이어 간의 데이터 변환을 담당하는 매퍼
 */
@Component
class AreaDtoMapper {

    /**
     * 검색 요청 DTO를 도메인 검색 조건으로 변환
     */
    fun toCriteria(request: AreaDto.SearchRequest): AreaCriteria.SearchAreaCriteria {
        return AreaCriteria.SearchAreaCriteria(
            keyword = request.keyword,
            regionId = request.regionId,
            latitude = request.latitude,
            longitude = request.longitude,
            radius = request.radius,
            offset = request.offset,
            limit = request.limit
        )
    }
    
    /**
     * 인구통계 기반 검색 조건으로 변환
     */
    fun toDemographicCriteria(request: AreaDto.SearchRequest): AreaCriteria.DemographicCriteria {
        return AreaCriteria.DemographicCriteria(
            minPopulation = request.minFloatingPopulation,
            baseSearchCriteria = toCriteria(request)
        )
    }
    
    /**
     * 상업 정보 기반 검색 조건으로 변환
     */
    fun toCommercialCriteria(request: AreaDto.SearchRequest): AreaCriteria.CommercialCriteria {
        return AreaCriteria.CommercialCriteria(
            minStoreCount = request.minStoreCount,
            maxRent = request.maxRent,
            baseSearchCriteria = toCriteria(request)
        )
    }

    /**
     * 도메인 DTO를 응답 DTO로 변환
     */
    fun toAreaResponse(info: AreaInfo.Main): AreaDto.AreaResponse {
        return AreaDto.AreaResponse(
            id = info.id,
            name = info.name,
            description = info.description,
            location = toLocationResponse(info.location),
        )
    }
    
    /**
     * 위치 정보 변환
     */
    private fun toLocationResponse(info: AreaInfo.LocationInfo): AreaDto.LocationResponse {
        return AreaDto.LocationResponse(
            latitude = info.latitude,
            longitude = info.longitude
        )
    }

    
    /**
     * 도메인 DTO 목록을 응답 DTO 목록으로 변환
     */
    fun toAreaResponses(infoList: List<AreaInfo.Main>): List<AreaDto.AreaResponse> {
        return infoList.map { toAreaResponse(it) }
    }
}
