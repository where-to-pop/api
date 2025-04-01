package com.wheretopop.application.area

import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.domain.area.AreaService
import org.springframework.stereotype.Service

@Service
class AreaFacade (
    private val areaService: AreaService
) {
    fun searchAreas(request: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main> {
        val areas = areaService.searchAreas(request)
        return areas
    }
}