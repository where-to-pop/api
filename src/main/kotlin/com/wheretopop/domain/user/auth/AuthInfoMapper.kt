package com.wheretopop.domain.user.auth

import java.time.Instant

/**
 * AuthUser 도메인 객체를 AuthInfo DTO로 변환하는 매퍼 클래스
 */
class AuthInfoMapper {
    companion object {
        /**
         * AuthUser 도메인 객체를 AuthInfo.Main으로 변환
         */
        fun toMainInfo(authUser: AuthUser): AuthInfo.Main {
            return AuthInfo.Main(
                id = authUser.id,
                userId = authUser.userId,
                identifier = authUser.identifier,
                createdAt = authUser.createdAt,
                updatedAt = authUser.updatedAt,
                deletedAt = authUser.deletedAt
            )
        }

        /**
         * token 정보와 만료 시간을 AuthInfo.Token으로 변환
         */
        fun toTokenInfo(
            accessToken: String,
            accessTokenExpiresIn: Long,
            refreshToken: String,
            refreshTokenExpiresIn: Long
        ): AuthInfo.Token {
            val now = Instant.now()
            return AuthInfo.Token(
                accessToken = accessToken,
                accessTokenExpiresAt = now.plusMillis(accessTokenExpiresIn),
                refreshToken = refreshToken,
                refreshTokenExpiresAt = now.plusMillis(refreshTokenExpiresIn)
            )
        }

        /**
         * AuthInfo.Token 생성 (Token과 AuthUser 함께 반환)
         */
        fun toAuthWithToken(
            authUser: AuthUser,
            token: AuthInfo.Token
        ): AuthInfo.AuthWithToken {
            return AuthInfo.AuthWithToken(
                auth = toMainInfo(authUser),
                token = token
            )
        }
    }
} 