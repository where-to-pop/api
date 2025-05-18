package com.wheretopop.domain.area

import com.wheretopop.shared.domain.identifier.AreaId

interface AreaInsightProvider{
    fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight?
    fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight>
}