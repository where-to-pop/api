package com.wheretopop.interfaces.user

import com.wheretopop.domain.user.UserInfo
import java.time.Instant

class UserDto {
    data class SignUpRequest(
        val username: String,
        val email: String,
        val identifier: String,
        val password: String,
        val profileImageUrl: String? = null
    )

    data class UserResponse(
        val id: String,
        val username: String,
        val email: String,
        val profileImageUrl: String?,
        val createdAt: Instant,
        val updatedAt: Instant
    ) {
        companion object {
            fun from(userInfo: UserInfo.Main): UserResponse {
                return UserResponse(
                    id = userInfo.id.toString(),
                    username = userInfo.username,
                    email = userInfo.email,
                    profileImageUrl = userInfo.profileImageUrl,
                    createdAt = userInfo.createdAt,
                    updatedAt = userInfo.updatedAt
                )
            }
        }
    }
} 