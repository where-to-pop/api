package com.wheretopop.domain.user.auth

import java.time.Instant

class RefreshToken private constructor(
    val id: RefreshTokenId,
    val userId: AuthUserId,
    val token: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {
    companion object {
        fun create(
            id: RefreshTokenId,
            userId: AuthUserId,
            token: String,
            expiresAt: Instant,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): RefreshToken {
            return RefreshToken(
                id,
                userId,
                token, 
                expiresAt,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
    
    /**
     * 토큰이 만료되었는지 확인
     */
    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt) || deletedAt != null
    }
    
    /**
     * 토큰의 유효성 확인
     */
    fun isValid(): Boolean {
        return !isExpired()
    }
} 