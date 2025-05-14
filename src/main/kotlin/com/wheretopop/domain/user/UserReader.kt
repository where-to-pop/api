package com.wheretopop.domain.user

interface UserReader {
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: String): User?
} 