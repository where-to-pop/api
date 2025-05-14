package com.wheretopop.domain.user

import java.time.Instant
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userReader: UserReader,
    private val userStore: UserStore
) : UserService {

    override suspend fun createUser(command: UserCommand.Create): UserInfo.Main {
        // 이메일 중복 검사
        userReader.findByEmail(command.email)?.let {
            throw IllegalArgumentException("이미 존재하는 이메일입니다: ${command.email}")
        }

        val user = User.create(
            id = UserId.create(),
            username = command.username,
            email = command.email,
            profileImageUrl = command.profileImageUrl,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedUser = userStore.save(user)
        return UserInfoMapper.toMainInfo(savedUser)
    }

    override suspend fun updateUser(command: UserCommand.Update): UserInfo.Main {
        val user = userReader.findById(command.id)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다: ${command.id}")

        val updatedUser = user.update(
            username = command.username,
            email = command.email,
            profileImageUrl = command.profileImageUrl
        )

        val savedUser = userStore.save(updatedUser)
        return UserInfoMapper.toMainInfo(savedUser)
    }
    
    override suspend fun findUserById(id: UserId): UserInfo.Main? {
        val user = userReader.findById(id) ?: return null
        return UserInfoMapper.toMainInfo(user)
    }
} 