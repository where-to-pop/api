package com.wheretopop.domain.user.auth

import com.wheretopop.domain.user.UserId
import java.time.Instant
import java.util.UUID

class AuthUser private constructor(
    val id: AuthUserId,
    val userId: UserId,
    val identifier: String,
    val password: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {
    companion object {
        fun create(
            id: AuthUserId,
            userId: UserId,
            identifier: String,
            password: String,
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
    
    /**
     * 리프레시 토큰 로테이션(Rotation) 기능
     * 현재 토큰을 무효화하고 새 토큰 생성
     */
    fun rotateRefreshToken(currentToken: RefreshToken, expirationTime: Long): RefreshToken {
        // 현재 토큰이 이미 만료되었는지 확인
        if (currentToken.isExpired()) {
            throw IllegalStateException("토큰이 이미 만료되었습니다")
        }
        
        // 현재 시간 기준으로 새 토큰 생성
        val now = Instant.now()
        val expiresAt = now.plusSeconds(expirationTime)
        
        // 새 토큰 생성
        return RefreshToken.create(
            id = RefreshTokenId.create(),
            userId = id,
            token = UUID.randomUUID().toString(),
            expiresAt = expiresAt,
            createdAt = now,
            updatedAt = now,
            deletedAt = null
        )
    }
} 