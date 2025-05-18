package com.wheretopop.application.user

import com.wheretopop.domain.user.UserInfo
import com.wheretopop.domain.user.auth.AuthInfo

interface UserFacade {
    fun signUp(input: UserInput.SignUp): UserInfo.Main
    fun login(input: UserInput.Authenticate): AuthInfo.Token
    fun refresh(input: UserInput.Refresh): AuthInfo.Token
    
}