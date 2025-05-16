package com.wheretopop.infrastructure.area.external

import com.wheretopop.application.area.AreaOpenDataUseCase
import com.wheretopop.application.area.AreaSnsUseCase
import com.wheretopop.domain.area.AreaId
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.domain.area.AreaInsightProvider
import org.springframework.stereotype.Component

@Component
class AreaExternalManager(
    private val areaExternalStore: AreaExternalStore,
    private val areaExternalReader: AreaExternalReader
): AreaOpenDataUseCase, AreaSnsUseCase, AreaInsightProvider {
    override suspend fun callOpenDataApiAndSave() {
        return areaExternalStore.callOpenDataApiAndSave()
    }

    override suspend fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight? {
        return areaExternalReader.findPopulationInsightByAreaId(areaId)
    }

    override suspend fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight> {
        return areaExternalReader.findPopulationInsightsByAreaIds(areaIds)
    }
}
