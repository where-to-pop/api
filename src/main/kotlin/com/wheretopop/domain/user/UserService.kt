package com.wheretopop.domain.user

interface UserService {
    fun createUser(command: UserCommand.CreateUser): UserInfo.Main
    fun updateUser(command: UserCommand.UpdateUser): UserInfo.Main
    fun findUserById(id: UserId): UserInfo.Main?
} 