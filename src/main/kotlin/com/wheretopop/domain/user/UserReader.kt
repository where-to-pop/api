package com.wheretopop.domain.user

interface UserReader {
    fun findById(id: UserId): User?
    fun findByEmail(email: String): User?
} 