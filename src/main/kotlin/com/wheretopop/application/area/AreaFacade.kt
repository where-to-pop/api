package com.wheretopop.application.area

import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.domain.area.AreaService
import com.wheretopop.shared.domain.identifier.AreaId
import mu.KotlinLogging
import org.springframework.stereotype.Service

/**
 * Area 애플리케이션 서비스
 * 컨트롤러와 도메인 서비스 사이의 퍼사드 역할
 */
@Service
class AreaFacade(
    private val areaService: AreaService,
    private val areaOpenDataUseCase: AreaOpenDataUseCase
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 필터 조건으로 지역 검색
     */
    fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main> {
        return areaService.searchAreas(criteria)
    }

    fun findAll(): List<AreaInfo.Main> {
        logger.info { "findAll() 호출" }
        val value =  areaService.findAll()
        logger.info { "findAll() - ${value.size}개 지역 정보 조회" }
        return value
    }

    fun getAreaDetailById(areaId: AreaId): AreaInfo.Detail? {
        return areaService.getAreaDetailById(areaId)
    }

    fun ingestAreaExternalData() {
        return areaOpenDataUseCase.callOpenDataApiAndSave()
    }

    fun findNearestArea(latitude: Double, longitude: Double): AreaInfo.Main? {
        return areaService.findNearestArea(latitude, longitude)
    }
}