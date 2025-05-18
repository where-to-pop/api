package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserId

/**
 * 인증 사용자 도메인 객체에 대한 저장소 인터페이스
 */
interface AuthUserRepository {
    fun findById(id: AuthUserId): AuthUser?
    fun findByUserId(userId: UserId): AuthUser?
    fun findByIdentifier(identifier: String): AuthUser?
    fun save(authUser: AuthUser): AuthUser
    fun save(authUsers: List<AuthUser>): List<AuthUser>
    fun deleteById(id: AuthUserId)
} 