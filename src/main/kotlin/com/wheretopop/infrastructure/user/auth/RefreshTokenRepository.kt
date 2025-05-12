package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.RefreshTokenId

/**
 * RefreshToken 도메인 객체에 대한 저장소 인터페이스
 */
interface RefreshTokenRepository {
    suspend fun findById(id: RefreshTokenId): RefreshToken?
    suspend fun findByToken(token: String): RefreshToken?
    suspend fun findByUserId(userId: AuthUserId): List<RefreshToken>
    suspend fun save(refreshToken: RefreshToken): RefreshToken
    suspend fun deleteById(id: RefreshTokenId)
    suspend fun deleteByToken(token: String)
} 