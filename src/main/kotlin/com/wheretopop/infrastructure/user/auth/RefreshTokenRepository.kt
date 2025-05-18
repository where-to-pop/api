package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.RefreshTokenId

/**
 * RefreshToken 도메인 객체에 대한 저장소 인터페이스
 */
interface RefreshTokenRepository {
    fun findById(id: RefreshTokenId): RefreshToken?
    fun findByToken(token: String): RefreshToken?
    fun findByUserId(userId: AuthUserId): List<RefreshToken>
    fun save(refreshToken: RefreshToken): RefreshToken
    fun deleteById(id: RefreshTokenId)
    fun deleteByToken(token: String)
} 