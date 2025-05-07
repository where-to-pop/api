package com.wheretopop.domain.user

import java.time.Instant

class User private constructor(
    val id: UserId,
    val username: String,
    val email: String,
    val profileImageUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {
    companion object {
        fun create(
            id: UserId,
            username: String,
            email: String,
            profileImageUrl: String?,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): User {
            return User(
                id,
                username,
                email,
                profileImageUrl,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
} 