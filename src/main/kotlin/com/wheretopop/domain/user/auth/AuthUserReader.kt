package com.wheretopop.domain.user.auth

interface AuthUserReader
{
    fun findAuthUserById(id: AuthUserId): AuthUser?
    fun findAuthUserByIdentifier(identifier: String): AuthUser?
}