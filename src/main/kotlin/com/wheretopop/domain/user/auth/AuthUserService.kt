package com.wheretopop.domain.user.auth

interface AuthUserService {
    suspend fun createAuthUser(
        command: AuthCommand.CreateAuthUser
    ): AuthUser

    suspend fun authenticate(
        command: AuthCommand.Authenticate
    ): AuthUser

    suspend fun refresh(
        command: AuthCommand.Refresh
    ): AuthUser

    suspend fun findAuthUserById(id: AuthUserId): AuthUser?

    suspend fun findAuthUserByIdentifier(identifier: String): AuthUser?
}