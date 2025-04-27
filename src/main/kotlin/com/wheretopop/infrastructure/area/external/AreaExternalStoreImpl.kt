package com.wheretopop.infrastructure.area.external

import com.wheretopop.infrastructure.area.external.opendata.OpenDataProcessor
import com.wheretopop.infrastructure.area.external.opendata.OpenDataType
import org.springframework.stereotype.Component

@Component
class AreaExternalStoreImpl(
    private val opendDataProcessor: List<OpenDataProcessor>
): AreaExternalStore {

    override suspend fun callOpenDataApiAndSave() {
        opendDataProcessor.forEach { processor ->
            processor.callAndSave()
        }
    }

    suspend fun callByType(requestedType: OpenDataType) {
        opendDataProcessor
            .firstOrNull { it.support(requestedType) }
            ?.callAndSave()
            ?: error("지원하지 않는 OpenDataType입니다: $requestedType")
    }
}
