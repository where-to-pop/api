package com.wheretopop.domain.user

interface UserService {
    suspend fun createUser(command: UserCommand.CreateUser): UserInfo.Main
    suspend fun updateUser(command: UserCommand.UpdateUser): UserInfo.Main
    suspend fun findUserById(id: UserId): UserInfo.Main?
} 