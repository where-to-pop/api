package com.wheretopop.application.area

import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.domain.area.AreaService
import org.springframework.stereotype.Service

/**
 * Area 애플리케이션 서비스
 * 컨트롤러와 도메인 서비스 사이의 퍼사드 역할
 */
@Service
class AreaFacade(
    private val areaService: AreaService
) {
    /**
     * 필터 조건으로 지역 검색
     */
    fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main> {
        return areaService.searchAreas(criteria)
    }
    
    /**
     * 기본 지역 데이터 초기화
     */
    fun initializeAreas(): List<AreaInfo.Main> {
        return areaService.initializeAreas()
    }
}