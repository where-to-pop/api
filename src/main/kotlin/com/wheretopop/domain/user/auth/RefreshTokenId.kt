package com.wheretopop.domain.user.auth

import com.wheretopop.shared.model.UniqueId

class RefreshTokenId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): RefreshTokenId {
            return RefreshTokenId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): RefreshTokenId {
            return RefreshTokenId(UniqueId.of(value).value)
        }
    }
}