package com.wheretopop.domain.user

interface UserStore {
    suspend fun save(user: User): User
} 