package com.wheretopop.application.user

import com.wheretopop.domain.user.UserInfo
import com.wheretopop.domain.user.auth.AuthInfo

interface UserFacade {
    suspend fun signUp(input: UserInput.SignUp): UserInfo.Main
    suspend fun login(input: UserInput.Authenticate): AuthInfo.Token
    suspend fun refresh(input: UserInput.Refresh): AuthInfo.Token
    
}