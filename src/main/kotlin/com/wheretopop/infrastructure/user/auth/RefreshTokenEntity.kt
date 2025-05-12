package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.RefreshTokenId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.Instant

class RefreshTokenEntity(
    val id: RefreshTokenId,
    val authUserId: AuthUserId,
    val token: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
) {
    companion object {
        fun of(refreshToken: RefreshToken): RefreshTokenEntity {
            return RefreshTokenEntity(
                id = refreshToken.id,
                authUserId = refreshToken.userId,
                token = refreshToken.token,
                expiresAt = refreshToken.expiresAt,
                createdAt = refreshToken.createdAt,
                updatedAt = refreshToken.updatedAt,
                deletedAt = refreshToken.deletedAt,

            )
        }
    }

    fun toDomain(): RefreshToken {
        return RefreshToken.create(
            id = id,
            userId = authUserId,
            token = token,
            expiresAt = expiresAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }

    fun update(refreshToken: RefreshToken): RefreshTokenEntity {
        return RefreshTokenEntity(
            id = id,
            authUserId = authUserId,
            token = refreshToken.token,
            expiresAt = refreshToken.expiresAt,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
}

@WritingConverter
class RefreshTokenIdToLongConverter : Converter<RefreshTokenId, Long> {
    override fun convert(source: RefreshTokenId) = source.toLong()
}

@ReadingConverter
class LongToRefreshTokenIdConverter : Converter<Long, RefreshTokenId> {
    override fun convert(source: Long) = RefreshTokenId.of(source)
}
