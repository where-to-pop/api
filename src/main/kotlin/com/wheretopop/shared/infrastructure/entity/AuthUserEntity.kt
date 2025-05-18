package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.AuthUserIdConverter
import com.wheretopop.config.PasswordConverter
import com.wheretopop.config.UserIdConverter
import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.Password
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.time.Instant

/**
 * 인증 사용자(AuthUser) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(
    name = "auth_users",
    uniqueConstraints = [UniqueConstraint(name = "uq_auth_users_identifier", columnNames = ["identifier"])]
)
@EntityListeners(AuditingEntityListener::class)
class AuthUserEntity(
    @Id
    @JdbcTypeCode(Types.BIGINT)
    @Convert(converter = AuthUserIdConverter::class)
    val id: AuthUserId,
    
    @Column(name = "user_id", nullable = false)
    @JdbcTypeCode(Types.BIGINT)
    @Convert(converter = UserIdConverter::class)
    val userId: UserId,
    
    @Column(nullable = false)
    var identifier: String,
    
    @Column(nullable = false)
    @Convert(converter = PasswordConverter::class)
    var password: Password,
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
    
    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
) {
    companion object {
        fun of(authUser: AuthUser): AuthUserEntity {
            return AuthUserEntity(
                id = authUser.id,
                userId = authUser.userId,
                identifier = authUser.identifier,
                password = authUser.password,
                createdAt = authUser.createdAt,
                updatedAt = authUser.updatedAt,
                deletedAt = authUser.deletedAt
            )
        }
    }

    fun toDomain(): AuthUser {
        return AuthUser.create(
            id = id,
            userId = userId,
            identifier = identifier,
            password = password,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }
}
