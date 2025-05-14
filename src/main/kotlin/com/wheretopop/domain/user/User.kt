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
    
    /**
     * 사용자 정보를 업데이트한 새 User 객체를 반환합니다.
     */
    fun update(
        username: String? = null,
        email: String? = null,
        profileImageUrl: String? = null
    ): User {
        return User(
            id = id,
            username = username ?: this.username,
            email = email ?: this.email,
            profileImageUrl = profileImageUrl ?: this.profileImageUrl,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
} 