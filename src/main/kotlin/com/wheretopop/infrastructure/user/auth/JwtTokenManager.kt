package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.*
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class JwtTokenManager(
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${jwt.secret}")
    private val secretKey: String,
    @Value("\${jwt.access-token-expiration}")
    private val accessTokenExpirationMs: Long,
    @Value("\${jwt.refresh-token-expiration}")
    private val refreshTokenExpirationMs: Long
) : TokenManager {
    override suspend fun issue(userId: UserId): AuthInfo.Token {
        // JWT 시크릿 키 생성
        val key = Keys.hmacShaKeyFor(secretKey.toByteArray())
        
        // 현재 시간과 만료 시간 계산
        val now = Date()
        val accessTokenExpiresIn = Date(now.time + accessTokenExpirationMs)
        val refreshTokenExpiresIn = Date(now.time + refreshTokenExpirationMs)
        
        // 액세스 토큰 생성
        val accessToken = Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
        
        // 리프레시 토큰 생성 (JWT 형식)
        val jti = UUID.randomUUID().toString() // 토큰 고유 ID
        val refreshToken = Jwts.builder()
            .setSubject(userId.toString())
            .setId(jti)
            .setIssuedAt(now)
            .setExpiration(refreshTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
        
        // 리프레시 토큰 도메인 객체 생성 (데이터베이스에 저장)
        val refreshTokenDomain = RefreshToken.create(
            id = RefreshTokenId.create(),
            userId = AuthUserId.of(userId.value),
            token = refreshToken,
            expiresAt = Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        // 리프레시 토큰 저장
        save(refreshTokenDomain)
        
        // 토큰 정보 반환
        return AuthInfo.Token(
            accessToken = accessToken,
            accessTokenExpiresIn = accessTokenExpirationMs,
            refreshToken = refreshToken,
            refreshTokenExpiresIn = refreshTokenExpirationMs
        )
    }

    override suspend fun save(refreshToken: RefreshToken): RefreshToken {
        return refreshTokenRepository.save(refreshToken)
    }

    override suspend fun load(rawRefreshToken: String): RefreshToken? {
        return refreshTokenRepository.findByToken(rawRefreshToken)
    }
}