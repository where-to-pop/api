package com.wheretopop.infrastructure.area.external

import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.shared.domain.identifier.AreaId

interface AreaExternalReader {
    fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight?
    fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight>
}