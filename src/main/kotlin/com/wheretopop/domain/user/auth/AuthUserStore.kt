package com.wheretopop.domain.user.auth

interface AuthUserStore
{
    suspend fun save(authUser: AuthUser): AuthUser
    suspend fun save(authUsers: List<AuthUser>): List<AuthUser>
}