package com.wheretopop.infrastructure.area.external

import com.wheretopop.application.area.AreaOpenDataUseCase
import com.wheretopop.application.area.AreaSnsUseCase
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.domain.area.AreaInsightProvider
import org.springframework.stereotype.Component

@Component
class AreaExternalManager(
    private val areaExternalStore: AreaExternalStore,
    private val areaExternalReader: AreaExternalReader
): AreaOpenDataUseCase, AreaSnsUseCase, AreaInsightProvider {
    override fun callOpenDataApiAndSave() {
        return areaExternalStore.callOpenDataApiAndSave()
    }

    override fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight? {
        return areaExternalReader.findPopulationInsightByAreaId(areaId)
    }

    override fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight> {
        return areaExternalReader.findPopulationInsightsByAreaIds(areaIds)
    }
}
