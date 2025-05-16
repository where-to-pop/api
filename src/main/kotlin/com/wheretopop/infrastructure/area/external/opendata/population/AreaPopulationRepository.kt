package com.wheretopop.infrastructure.area.external.opendata.population

import com.wheretopop.domain.area.AreaId
import com.wheretopop.domain.area.AreaInfo

interface AreaPopulationRepository {
    suspend fun save(entity: AreaPopulationEntity): AreaPopulationEntity
    suspend fun save(entities: List<AreaPopulationEntity>): List<AreaPopulationEntity>
    suspend fun findLatestByAreaId(areaId: AreaId): AreaPopulationEntity?
    suspend fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight?
    suspend fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight>
}