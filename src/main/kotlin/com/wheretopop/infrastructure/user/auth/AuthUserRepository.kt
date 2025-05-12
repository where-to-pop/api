package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserId

/**
 * 인증 사용자 도메인 객체에 대한 저장소 인터페이스
 */
interface AuthUserRepository {
    suspend fun findById(id: AuthUserId): AuthUser?
    suspend fun findByUserId(userId: UserId): AuthUser?
    suspend fun findByIdentifier(identifier: String): AuthUser?
    suspend fun save(authUser: AuthUser): AuthUser
    suspend fun save(authUsers: List<AuthUser>): List<AuthUser>
    suspend fun deleteById(id: AuthUserId)
} 