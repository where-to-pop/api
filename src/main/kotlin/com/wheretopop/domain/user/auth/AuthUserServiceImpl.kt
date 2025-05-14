package com.wheretopop.domain.user.auth


import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
            throw ErrorCode.AUTH_IDENTIFIER_ALREADY_EXISTS.toException()
        }
        val authUser = command.toDomain();
        return AuthInfoMapper.toMainInfo(authUserStore.save(authUser));
    }

    @Transactional
    override suspend fun authenticate(command: AuthCommand.Authenticate): AuthInfo.Token {
        val (identifier, rawPassword) = command
        val authUser = authUserReader.findAuthUserByIdentifier(identifier)
            ?: throw ErrorCode.AUTH_INVALID_IDENTIFIER.toException()
        val isLoginSuccess = authUser.authenticate(identifier, rawPassword)
        if (!isLoginSuccess) {
            throw ErrorCode.AUTH_INVALID_PASSWORD.toException()
        }
        val tokens = tokenManager.issue(authUser)
        return tokens
    }

    @Transactional
    override suspend fun refresh(command: AuthCommand.Refresh): AuthInfo.Token {
        val (rawRefreshToken, userId) = command
        val existingRefreshToken: RefreshToken = tokenManager.load(rawRefreshToken) ?:
            throw ErrorCode.AUTH_INVALID_TOKEN.toException()
        existingRefreshToken.takeIf { it.isValid() } ?: throw ErrorCode.AUTH_INVALID_TOKEN.toException()
        val authUser = authUserReader.findAuthUserById(existingRefreshToken.authUserId)
            ?: throw ErrorCode.AUTH_INVALID_TOKEN.toException()
        authUser.userId != userId && throw ErrorCode.AUTH_INVALID_TOKEN.toException()

        val newTokens = tokenManager.issue(authUser)
        val revokedExistingRefreshToken = existingRefreshToken.revoke()
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