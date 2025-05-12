package com.wheretopop.domain.user.auth

interface AuthUserService {
    suspend fun createAuthUser(
        command: AuthCommand.CreateAuthUser
    ): AuthInfo.Main

    suspend fun authenticate(
        command: AuthCommand.Authenticate
    ): AuthInfo.Token

    suspend fun refresh(
        command: AuthCommand.Refresh
    ): AuthInfo.Token

    suspend fun findAuthUserById(id: AuthUserId): AuthInfo.Main?

    suspend fun findAuthUserByIdentifier(identifier: String): AuthInfo.Main?
}