package com.wheretopop.config.security

import com.wheretopop.domain.user.UserId
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * JWT 토큰 관련 정보를 담는 DTO
 */
data class TokenDto(
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long,
    val accessTokenExpiresAt: Instant,
    val refreshTokenExpiresAt: Instant
)

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,
    @Value("\${jwt.access-token-expiration}")
    private val accessTokenExpirationMs: Long,
    @Value("\${jwt.refresh-token-expiration}")
    private val refreshTokenExpirationMs: Long
) {
    private val key: Key by lazy { Keys.hmacShaKeyFor(secretKey.toByteArray()) }

    companion object {
        const val USER_ID_CLAIM = "user_id"
        const val TOKEN_TYPE_CLAIM = "token_type"
        const val ACCESS_TOKEN = "access"
        const val REFRESH_TOKEN = "refresh"
    }

    /**
     * 액세스 토큰과 리프레시 토큰을 생성하여 TokenDto 객체로 반환합니다.
     */
    fun generateTokens(userId: UserId): TokenDto {
        // 현재 시간과 만료 시간 계산
        val now = Date()
        val accessTokenExpiration = Date(now.time + accessTokenExpirationMs)
        val refreshTokenExpiration = Date(now.time + refreshTokenExpirationMs)
        
        // 액세스 토큰 생성
        val accessToken = Jwts.builder()
            .setSubject(userId.toString())
            .claim(USER_ID_CLAIM, userId.toLong())
            .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN)
            .setIssuedAt(now)
            .setExpiration(accessTokenExpiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
        
        // 리프레시 토큰 생성
        val jti = UUID.randomUUID().toString() // 토큰 고유 ID
        val refreshToken = Jwts.builder()
            .setSubject(userId.toString())
            .setId(jti)
            .claim(USER_ID_CLAIM, userId.toLong())
            .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN)
            .setIssuedAt(now)
            .setExpiration(refreshTokenExpiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
        
        // TokenDto 생성 및 반환
        return TokenDto(
            accessToken = accessToken,
            accessTokenExpiresIn = accessTokenExpirationMs,
            refreshToken = refreshToken,
            refreshTokenExpiresIn = refreshTokenExpirationMs,
            accessTokenExpiresAt = getAccessTokenExpirationInstant(),
            refreshTokenExpiresAt = getRefreshTokenExpirationInstant()
        )
    }

    /**
     * 액세스 토큰을 생성합니다.
     */
    fun generateAccessToken(userId: UserId): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpirationMs)

        return Jwts.builder()
            .setSubject(userId.toString())
            .claim(USER_ID_CLAIM, userId.toLong())
            .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * 리프레시 토큰을 생성합니다.
     */
    fun generateRefreshToken(userId: UserId): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpirationMs)
        val jti = UUID.randomUUID().toString() // 토큰 고유 ID

        return Jwts.builder()
            .setSubject(userId.toString())
            .setId(jti)
            .claim(USER_ID_CLAIM, userId.toLong())
            .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     */
    fun getUserIdFromToken(token: String): UserId? {
        try {
            val claims = parseClaims(token)
            val userIdValue = claims[USER_ID_CLAIM] as Long
            return UserId.of(userIdValue)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 토큰에서 사용자 ID 값을 추출합니다.
     */
    fun getUserIdValueFromToken(token: String): Long? {
        try {
            val claims = parseClaims(token)
            return claims[USER_ID_CLAIM] as Long
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 액세스 토큰의 유효성을 검증합니다.
     */
    fun validateAccessToken(token: String): Boolean {
        return try {
            val claims = parseClaims(token)
            val tokenType = claims[TOKEN_TYPE_CLAIM] as String?
            tokenType == ACCESS_TOKEN && !isTokenExpired(claims)
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException,
                is UnsupportedJwtException,
                is MalformedJwtException -> false
                else -> false
            }
        }
    }

    /**
     * 리프레시 토큰의 유효성을 검증합니다.
     */
    fun validateRefreshToken(token: String): Boolean {
        return try {
            val claims = parseClaims(token)
            val tokenType = claims[TOKEN_TYPE_CLAIM] as String?
            tokenType == REFRESH_TOKEN && !isTokenExpired(claims)
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException,
                is UnsupportedJwtException,
                is MalformedJwtException -> false
                else -> false
            }
        }
    }

    /**
     * 토큰의 만료 시간을 확인합니다.
     */
    private fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }

    /**
     * 토큰을 파싱하여 클레임을 추출합니다.
     */
    private fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    /**
     * 액세스 토큰 만료 시간을 반환합니다.
     */
    fun getAccessTokenExpirationMs(): Long = accessTokenExpirationMs

    /**
     * 리프레시 토큰 만료 시간을 반환합니다.
     */
    fun getRefreshTokenExpirationMs(): Long = refreshTokenExpirationMs

    /**
     * 액세스 토큰 만료 시간을 Instant로 반환합니다.
     */
    fun getAccessTokenExpirationInstant(): Instant {
        return Instant.now().plus(accessTokenExpirationMs, ChronoUnit.MILLIS)
    }

    /**
     * 리프레시 토큰 만료 시간을 Instant로 반환합니다.
     */
    fun getRefreshTokenExpirationInstant(): Instant {
        return Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS)
    }

    /**
     * 사용자 ID로부터 Authentication 객체를 생성합니다.
     * Spring Security에서 사용합니다.
     */
    fun getAuthentication(userId: UserId): Authentication {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        return UsernamePasswordAuthenticationToken(userId, null, authorities)
    }
} 