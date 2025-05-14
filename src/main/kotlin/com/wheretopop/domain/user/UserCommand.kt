package com.wheretopop.domain.user

class UserCommand {
    data class CreateUser(
        val username: String,
        val email: String,
        val profileImageUrl: String?
    )

    data class UpdateUser(
        val id: UserId,
        val username: String,
        val email: String,
        val profileImageUrl: String?
    )
}