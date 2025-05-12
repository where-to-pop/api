package com.wheretopop.domain.user.auth

import com.wheretopop.domain.user.UserId
import java.time.Instant

class AuthInfo {
    data class Main(
        val id: AuthUserId,
        val userId: UserId,
        val identifier: String,
        val createdAt: Instant,
        val updatedAt: Instant,
        val deletedAt: Instant?,
    )

    data class Token(
        val accessToken: String,
        val accessTokenExpiresAt: Instant,
        val refreshToken: String,
        val refreshTokenExpiresAt: Instant,
    )
}