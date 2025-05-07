package com.wheretopop.domain.user

import com.wheretopop.shared.model.UniqueId

class UserId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): UserId {
            return UserId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): UserId {
            return UserId(UniqueId.of(value).value)
        }
    }
}
