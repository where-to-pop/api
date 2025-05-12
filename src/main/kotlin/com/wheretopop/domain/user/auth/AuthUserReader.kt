package com.wheretopop.domain.user.auth

interface AuthUserReader
{
    suspend fun findAuthUserById(id: AuthUserId): AuthUser?
    suspend fun findAuthUserByIdentifier(identifier: String): AuthUser?
}