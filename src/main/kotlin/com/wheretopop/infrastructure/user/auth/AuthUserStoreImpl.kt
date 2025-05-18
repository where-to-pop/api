package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserStore
import org.springframework.stereotype.Component

@Component
class AuthUserStoreImpl(
    private val authUserRepository: AuthUserRepository
) : AuthUserStore {
    override fun save(authUser: AuthUser): AuthUser {
        return authUserRepository.save(authUser)
    }

    override fun save(authUsers: List<AuthUser>): List<AuthUser> {
        return authUserRepository.save(authUsers)
    }
}