package com.wheretopop.domain.user.auth

interface AuthUserService {
    fun createAuthUser(
        command: AuthCommand.CreateAuthUser
    ): AuthInfo.Main

    fun authenticate(
        command: AuthCommand.Authenticate
    ): AuthInfo.Token

    fun refresh(
        command: AuthCommand.Refresh
    ): AuthInfo.Token

    fun findAuthUserById(id: AuthUserId): AuthInfo.Main?
    fun findAuthUserByIdentifier(identifier: String): AuthInfo.Main?
}