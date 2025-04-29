package com.wheretopop.domain.building.register

import com.wheretopop.shared.model.UniqueId

class BuildingRegisterId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): BuildingRegisterId {
            return BuildingRegisterId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): BuildingRegisterId {
            return BuildingRegisterId(UniqueId.of(value).value)
        }
    }
}
