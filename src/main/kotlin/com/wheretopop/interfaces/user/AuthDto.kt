package com.wheretopop.interfaces.user

import com.wheretopop.domain.user.auth.AuthInfo
import java.time.Instant

class AuthDto {
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class TokenResponse(
        val accessToken: String,
        val accessTokenExpiresAt: Instant,
        val refreshTokenExpiresAt: Instant
    ) {
        companion object {
            fun from(token: AuthInfo.Token): TokenResponse {
                return TokenResponse(
                    accessToken = token.accessToken,
                    accessTokenExpiresAt = token.accessTokenExpiresAt,
                    refreshTokenExpiresAt = token.refreshTokenExpiresAt
                )
            }
        }
    }
} 