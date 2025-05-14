package com.wheretopop.infrastructure.user

import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserId

internal interface UserRepository {
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: String): User?
    suspend fun save(user: User): User
}