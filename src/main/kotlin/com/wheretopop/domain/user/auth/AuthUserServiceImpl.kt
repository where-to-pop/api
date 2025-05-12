package com.wheretopop.domain.user.auth

import com.wheretopop.shared.exception.AuthIdentifierAlreadyExistsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUserServiceImpl(
    private val authUserStore: AuthUserStore,
    private val authUserReader: AuthUserReader
): AuthUserService {

    @Transactional
    override suspend fun createAuthUser(command: AuthCommand.CreateAuthUser): AuthUser {
        val exists = authUserReader.findAuthUserByIdentifier(command.identifier) != null
        if (exists) {
            throw AuthIdentifierAlreadyExistsException("이미 존재하는 아이디입니다.")
        }
        val authUser = command.toDomain();
        return authUserStore.save(authUser)
    }

    @Transactional
    override suspend fun authenticate(command: AuthCommand.Authenticate): AuthUser {
        val (identifier, rawPassword) = command
        val authUser = authUserReader.findAuthUserByIdentifier(identifier)
            ?: throw AuthIdentifierAlreadyExistsException()
        val isLoginSuccess = authUser.authenticate(identifier, rawPassword)
        return authUser
    }

    override suspend fun refresh(command: AuthCommand.Refresh): AuthUser {
        TODO("Not yet implemented")
    }

    override suspend fun findAuthUserById(id: AuthUserId): AuthUser? {
        return authUserReader.findAuthUserById(id)
    }

    override suspend fun findAuthUserByIdentifier(identifier: String): AuthUser? {
        return authUserReader.findAuthUserByIdentifier(identifier)
    }
}