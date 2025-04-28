package com.wheretopop.infrastructure.area.external

import org.springframework.stereotype.Component
import com.wheretopop.application.area.AreaOpenDataUseCase
import com.wheretopop.application.area.AreaSnsUseCase

@Component
class AreaExternalManager(
    private val areaExternalStore: AreaExternalStore,
    private val areaExternalReader: AreaExternalReader
): AreaOpenDataUseCase, AreaSnsUseCase {
    override suspend fun callOpenDataApiAndSave() {
        return areaExternalStore.callOpenDataApiAndSave()
    }
}
