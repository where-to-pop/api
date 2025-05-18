package com.wheretopop.domain.user.auth

interface AuthUserStore
{
    fun save(authUser: AuthUser): AuthUser
    fun save(authUsers: List<AuthUser>): List<AuthUser>
}