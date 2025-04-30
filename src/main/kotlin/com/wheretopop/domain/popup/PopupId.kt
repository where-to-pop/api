package com.wheretopop.domain.popup

import com.wheretopop.shared.model.UniqueId

class PopupId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): PopupId {
            return PopupId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): PopupId {
            return PopupId(UniqueId.of(value).value)
        }
    }
}