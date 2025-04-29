package com.wheretopop.domain.building.core

import com.wheretopop.shared.model.UniqueId

class BuildingId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): BuildingId {
            return BuildingId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): BuildingId {
            return BuildingId(UniqueId.of(value).value)
        }
    }
}
