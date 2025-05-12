package com.wheretopop.domain.user.auth

import com.wheretopop.shared.exception.AuthIdentifierAlreadyExistsException
import com.wheretopop.shared.exception.AuthInvalidIdentifierException
import com.wheretopop.shared.exception.AuthInvalidPasswordException
import com.wheretopop.shared.exception.AuthInvalidTokenException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AuthUserServiceImpl(
    private val authUserStore: AuthUserStore,
    private val authUserReader: AuthUserReader,
    private val tokenManager: TokenManager
): AuthUserService {

    @Transactional
    override suspend fun createAuthUser(command: AuthCommand.CreateAuthUser): AuthInfo.Main {
        val exists = authUserReader.findAuthUserByIdentifier(command.identifier) != null
        if (exists) {
            throw AuthIdentifierAlreadyExistsException("이미 존재하는 아이디입니다.")
        }
        val authUser = command.toDomain();
        return AuthInfoMapper.toMainInfo(authUserStore.save(authUser));
    }

    @Transactional
    override suspend fun authenticate(command: AuthCommand.Authenticate): AuthInfo.Token {
        val (identifier, rawPassword) = command
        val authUser = authUserReader.findAuthUserByIdentifier(identifier)
            ?: throw AuthInvalidIdentifierException()
        val isLoginSuccess = authUser.authenticate(identifier, rawPassword)
        if (!isLoginSuccess) {
            throw AuthInvalidPasswordException()
        }
        val tokens = tokenManager.issue(authUser.userId)
        return tokens
    }

    @Transactional
    override suspend fun refresh(command: AuthCommand.Refresh): AuthInfo.Token {
        val (rawRefreshToken, userId) = command
        val existingRefreshToken: RefreshToken = tokenManager.load(rawRefreshToken) ?:
            throw AuthInvalidTokenException()
        existingRefreshToken.takeIf { it.isValid() } ?: throw AuthInvalidTokenException()
        val authUser = authUserReader.findAuthUserById(existingRefreshToken.authUserId)
            ?: throw AuthInvalidTokenException()
        authUser.userId != userId && throw AuthInvalidTokenException()

        val newTokens = tokenManager.issue(userId)
        val newRefreshToken = RefreshToken.create(
            authUserId = authUser.id,
            token = newTokens.refreshToken,
            expiresAt = newTokens.refreshTokenExpiresAt,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )
        val revokedExistingRefreshToken = existingRefreshToken.revoke()
        tokenManager.save(newRefreshToken)
        tokenManager.save(revokedExistingRefreshToken)
        return newTokens
    }

    override suspend fun findAuthUserById(id: AuthUserId): AuthInfo.Main? {
        return authUserReader.findAuthUserById(id).let {
            it?.let { authUser ->
                AuthInfoMapper.toMainInfo(authUser)
            }
        }
    }

    override suspend fun findAuthUserByIdentifier(identifier: String): AuthInfo.Main? {
        return authUserReader.findAuthUserByIdentifier(identifier).let {
            it?.let { authUser ->
                AuthInfoMapper.toMainInfo(authUser)
            }
        }

    }
}