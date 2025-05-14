package com.wheretopop.domain.user

import java.time.Instant

class UserInfo {
    data class Main(
        val id: Long,
        val username: String,
        val email: String,
        val profileImageUrl: String?,
        val createdAt: Instant,
        val updatedAt: Instant,
        val deletedAt: Instant?
    )
} 