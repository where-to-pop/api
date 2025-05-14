package com.wheretopop.domain.user

class UserCommand {
    data class Create(
        val username: String,
        val email: String,
        val profileImageUrl: String?
    )

    data class Update(
        val id: UserId,
        val username: String,
        val email: String,
        val profileImageUrl: String?
    )
} 