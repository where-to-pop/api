package com.wheretopop.domain.user

interface UserStore {
    fun save(user: User): User
} 