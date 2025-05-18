package com.wheretopop.shared.domain.identifier

import com.wheretopop.shared.model.UniqueId

class XId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): XId {
            return XId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): XId {
            return XId(UniqueId.of(value).value)
        }
    }
}
