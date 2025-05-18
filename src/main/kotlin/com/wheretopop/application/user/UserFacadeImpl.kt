package com.wheretopop.application.user

import com.wheretopop.domain.user.UserInfo
import com.wheretopop.domain.user.UserService
import com.wheretopop.domain.user.auth.AuthInfo
import com.wheretopop.domain.user.auth.AuthUserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserFacadeImpl(
    private val userService: UserService,
    private val authUserService: AuthUserService
) : UserFacade {

    @Transactional
    override fun signUp(
        input: UserInput.SignUp
    ): UserInfo.Main {
        val user = userService.createUser(input.toCommand())
        val authUser = authUserService.createAuthUser(input.toCommand(user))
        return user
    }

    override fun login(input: UserInput.Authenticate): AuthInfo.Token {
        val token = authUserService.authenticate(input.toCommand())
        return token
    }

    override fun refresh(input: UserInput.Refresh): AuthInfo.Token {
        val token = authUserService.refresh(input.toCommand())
        return token
    }
}