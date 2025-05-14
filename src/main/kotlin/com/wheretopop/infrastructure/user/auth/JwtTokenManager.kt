package com.wheretopop.infrastructure.user.auth

import com.wheretopop.config.security.JwtProvider
import com.wheretopop.domain.user.auth.*
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtTokenManager(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider
) : TokenManager {
    override suspend fun issue(authUser: AuthUser): AuthInfo.Token {
        // JwtProvider를 통해 토큰 생성
        val tokenDto = jwtProvider.generateTokens(authUser.userId)
        
        // 리프레시 토큰 도메인 객체 생성 (데이터베이스에 저장)
        val refreshTokenDomain = RefreshToken.create(
            id = RefreshTokenId.create(),
            authUserId = authUser.id,
            token = tokenDto.refreshToken,
            expiresAt = tokenDto.refreshTokenExpiresAt,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        // 리프레시 토큰 저장
        save(refreshTokenDomain)
        
        // TokenDto를 AuthInfo.Token으로 변환하여 반환
        return AuthInfo.Token(
            accessToken = tokenDto.accessToken,
            accessTokenExpiresAt = tokenDto.accessTokenExpiresAt,
            refreshToken = tokenDto.refreshToken,
            refreshTokenExpiresAt = tokenDto.refreshTokenExpiresAt
        )
    }

    override suspend fun save(refreshToken: RefreshToken): RefreshToken {
        return refreshTokenRepository.save(refreshToken)
    }

    override suspend fun load(rawRefreshToken: String): RefreshToken? {
        return refreshTokenRepository.findByToken(rawRefreshToken)
    }
}