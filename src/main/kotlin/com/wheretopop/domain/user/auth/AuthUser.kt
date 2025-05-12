package com.wheretopop.domain.user.auth

import com.wheretopop.domain.user.UserId
import java.time.Instant

class AuthUser private constructor(
    val id: AuthUserId,
    val userId: UserId,
    val identifier: String,
    val password: Password,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?
) {
    companion object {
        fun create(
            id: AuthUserId = AuthUserId.create(),
            userId: UserId,
            identifier: String,
            password: Password,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): AuthUser {
            return AuthUser(
                id,
                userId,
                identifier,
                password,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }


    fun authenticate(
        identifier: String,
        rawPassword: String
    ): Boolean {
        return this.identifier == identifier && this.password.matches(rawPassword)
    }
} 