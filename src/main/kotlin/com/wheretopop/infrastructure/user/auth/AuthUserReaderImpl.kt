package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.AuthUserReader
import org.springframework.stereotype.Component

@Component
class AuthUserReaderImpl(
    private val authUserRepository: AuthUserRepository
) : AuthUserReader {
    override suspend fun findAuthUserById(id: AuthUserId): AuthUser? {
        return authUserRepository.findById(id)
    }

    override suspend fun findAuthUserByIdentifier(identifier: String): AuthUser? {
        return authUserRepository.findByIdentifier(identifier)
    }
}