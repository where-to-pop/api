package com.wheretopop.shared.domain.identifier

import com.wheretopop.shared.model.UniqueId

class PopupPopplyId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): PopupPopplyId {
            return PopupPopplyId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): PopupPopplyId {
            return PopupPopplyId(UniqueId.of(value).value)
        }
    }
}