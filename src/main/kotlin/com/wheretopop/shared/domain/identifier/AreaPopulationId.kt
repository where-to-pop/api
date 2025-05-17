package com.wheretopop.shared.domain.identifier

import com.wheretopop.shared.model.UniqueId

class AreaPopulationId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): AreaPopulationId {
            return AreaPopulationId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): AreaPopulationId {
            return AreaPopulationId(UniqueId.of(value).value)
        }
    }
}
