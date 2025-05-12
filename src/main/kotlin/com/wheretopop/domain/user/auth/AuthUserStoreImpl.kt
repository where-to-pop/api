package com.wheretopop.domain.user.auth

import org.springframework.stereotype.Component

@Component
class AuthUserStoreImpl : AuthUserStore {
    override suspend fun save(authUser: AuthUser): AuthUser {
        TODO("Not yet implemented")
    }

    override suspend fun save(authUsers: List<AuthUser>): List<AuthUser> {
        TODO("Not yet implemented")
    }
}