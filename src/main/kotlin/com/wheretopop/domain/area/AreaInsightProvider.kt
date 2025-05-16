package com.wheretopop.domain.area

interface AreaInsightProvider{
    suspend fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight?
    suspend fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight>
}