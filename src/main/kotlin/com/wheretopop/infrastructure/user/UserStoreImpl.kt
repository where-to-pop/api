package com.wheretopop.infrastructure.user

import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserStore
import org.springframework.stereotype.Component

@Component
internal class UserStoreImpl(
    private val userRepository: UserRepository
) : UserStore {
    override fun save(user: User): User {
        return userRepository.save(user)
    }
} 