package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthInfo
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.TokenManager
import org.springframework.stereotype.Component

@Component
class JwtTokenManager : TokenManager {
    override suspend fun issue(userId: UserId): AuthInfo.Token {
        TODO("Not yet implemented")
    }

    override suspend fun save(refreshToken: RefreshToken): RefreshToken {
        TODO("Not yet implemented")
    }

    override suspend fun load(rawRefreshToken: String): RefreshToken? {
        TODO("Not yet implemented")
    }
}