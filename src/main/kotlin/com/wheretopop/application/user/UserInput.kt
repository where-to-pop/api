package com.wheretopop.application.user

import com.wheretopop.domain.user.UserCommand
import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.UserInfo
import com.wheretopop.domain.user.auth.AuthCommand

class UserInput {
    data class SignUp(
        val username: String,
        val email: String,
        val identifier: String,
        val rawPassword: String,
        val profileImageUrl: String? = null
    ) {
        fun toCommand(): UserCommand.CreateUser {
            return UserCommand.CreateUser(
                username = username,
                email = email,
                profileImageUrl = profileImageUrl
            )
        }

        fun toCommand(userInfo: UserInfo.Main): AuthCommand.CreateAuthUser {
            return AuthCommand.CreateAuthUser(
                userId = userInfo.id,
                identifier = identifier,
                rawPassword = rawPassword
            )
        }
    }

    data class Authenticate(
        val identifier: String,
        val rawPassword: String
    ) {
        fun toCommand(): AuthCommand.Authenticate {
            return AuthCommand.Authenticate(
                identifier = identifier,
                rawPassword = rawPassword
            )
        }
    }

    data class Refresh(
        val rawRefreshToken: String,
        val userId: UserId,
    ) {
        fun toCommand(): AuthCommand.Refresh {
            return AuthCommand.Refresh(
                rawRefreshToken = rawRefreshToken,
                userId = userId
            )
        }
    }

}