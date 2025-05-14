package com.wheretopop.domain.user

interface UserService {
    suspend fun createUser(command: UserCommand.Create): UserInfo.Main
    suspend fun updateUser(command: UserCommand.Update): UserInfo.Main
    suspend fun findUserById(id: UserId): UserInfo.Main?
} 