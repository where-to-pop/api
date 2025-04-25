package com.wheretopop.domain.area

import com.wheretopop.shared.model.UniqueId

class AreaId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): AreaId {
            return AreaId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): AreaId {
            return AreaId(UniqueId.of(value).value)
        }
    }
}
