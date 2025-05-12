package com.wheretopop.domain.user.auth

import org.springframework.stereotype.Component

@Component
class AuthUserReaderImpl : AuthUserReader {
    override suspend fun findAuthUserById(id: AuthUserId): AuthUser? {
        TODO("Not yet implemented")
    }

    override suspend fun findAuthUserByIdentifier(identifier: String): AuthUser? {
        TODO("Not yet implemented")
    }
}