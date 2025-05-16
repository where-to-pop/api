package com.wheretopop.infrastructure.area.external

import com.wheretopop.domain.area.AreaId
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.infrastructure.area.external.opendata.population.AreaPopulationRepository
import org.springframework.stereotype.Component

@Component
class AreaExternalReaderImpl (
    private val areaPopulationRepository: AreaPopulationRepository,
) : AreaExternalReader {
    override suspend fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight? {
        return areaPopulationRepository.findPopulationInsightByAreaId(areaId)
    }

    override suspend fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight> {
        return areaPopulationRepository.findPopulationInsightsByAreaIds(areaIds)
    }
}