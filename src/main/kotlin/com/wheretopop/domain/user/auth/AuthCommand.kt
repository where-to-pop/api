package com.wheretopop.domain.user.auth

import com.wheretopop.domain.user.UserId
import java.time.Instant

class AuthCommand {
    data class CreateAuthUser(
        val userId: UserId,
        val identifier: String,
        val rawPassword: String,
    ){
        fun toDomain(): AuthUser {
            return AuthUser.create(
                userId = userId,
                identifier = identifier,
                password = Password.of(rawPassword),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null
            )
        }
    }

    data class Authenticate(
        val identifier: String,
        val rawPassword: String
    )

    data class Refresh(
        val rawRefreshToken: String,
        val userId: UserId,
    )
}