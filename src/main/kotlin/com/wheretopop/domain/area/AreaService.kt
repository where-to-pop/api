package com.wheretopop.domain.area

import com.wheretopop.shared.domain.identifier.AreaId

interface AreaService {
    fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main>
    fun findAll(): List<AreaInfo.Main>
    fun getAreaDetailById(id: AreaId): AreaInfo.Detail
    fun searchNearest(latitude: Double, longitude: Double): AreaInfo.Main?
    fun findNearestArea(latitude: Double, longitude: Double): AreaInfo.Main?
}