package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.RefreshTokenId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("refresh_tokens")
internal class RefreshTokenEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: RefreshTokenId,
    @Column("auth_user_id")
    val authUserId: AuthUserId,
    @Column("token")
    val token: String,
    @Column("expires_at")
    val expiresAt: Instant,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        fun of(refreshToken: RefreshToken): RefreshTokenEntity {
            return RefreshTokenEntity(
                id = refreshToken.id,
                authUserId = refreshToken.authUserId,
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
            authUserId = authUserId,
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
