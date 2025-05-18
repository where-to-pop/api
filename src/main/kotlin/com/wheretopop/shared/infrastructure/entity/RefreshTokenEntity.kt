package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.RefreshTokenId
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.time.Instant

/**
 * 리프레시 토큰(RefreshToken) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "refresh_tokens")
@EntityListeners(AuditingEntityListener::class)
class RefreshTokenEntity(
    @Id
    @JdbcTypeCode(Types.BIGINT)
    val id: Long,
    
    @Column(name = "auth_user_id", nullable = false)
    val authUserId: Long,
    
    @Column(nullable = false)
    val token: String,
    
    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
    
    @Column(name = "deleted_at")
    val deletedAt: Instant? = null
) {
    companion object {
        fun of(refreshToken: RefreshToken): RefreshTokenEntity {
            return RefreshTokenEntity(
                id = refreshToken.id.toLong(),
                authUserId = refreshToken.authUserId.toLong(),
                token = refreshToken.token,
                expiresAt = refreshToken.expiresAt,
                createdAt = refreshToken.createdAt,
                updatedAt = refreshToken.updatedAt,
                deletedAt = refreshToken.deletedAt
            )
        }
    }

    fun toDomain(): RefreshToken {
        return RefreshToken.create(
            id = RefreshTokenId.of(id),
            authUserId = AuthUserId.of(authUserId),
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
