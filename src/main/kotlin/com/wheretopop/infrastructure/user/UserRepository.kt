package com.wheretopop.infrastructure.user

import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserId

internal interface UserRepository {
    fun findById(id: UserId): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
}