package com.wheretopop.domain.user.auth

class AuthInfo {
    data class Token(
        val accessToken: String,
        val accessTokenExpiresIn: Long,
        val refreshToken: String,
        val refreshTokenExpiresIn: Long,
    )
}