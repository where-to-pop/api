package com.wheretopop.infrastructure.user

import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.UserReader
import org.springframework.stereotype.Component

@Component
internal class UserReaderImpl(
    private val userRepository: UserRepository
) : UserReader {
    override suspend fun findById(id: UserId): User? {
        return userRepository.findById(id)
    }

    override suspend fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
} 