package com.wheretopop.domain.user.auth

import com.wheretopop.shared.model.UniqueId

class AuthUserId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): AuthUserId {
            return AuthUserId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): AuthUserId {
            return AuthUserId(UniqueId.of(value).value)
        }
    }
}