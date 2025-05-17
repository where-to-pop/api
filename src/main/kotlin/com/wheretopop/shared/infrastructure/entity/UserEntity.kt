package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.JpaConverterConfig
import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserId
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/**
 * 사용자(User) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(name = "uq_users_email", columnNames = ["email"])]
)
@EntityListeners(AuditingEntityListener::class)
class UserEntity(
    @Id
    @Convert(converter = JpaConverterConfig.UserIdConverter::class)
    val id: UserId,
    
    @Column(nullable = false)
    var username: String,
    
    @Column(nullable = false)
    var email: String,
    
    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,
    
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
        fun of(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                email = user.email,
                profileImageUrl = user.profileImageUrl,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                deletedAt = user.deletedAt
            )
        }
    }

    fun toDomain(): User {
        return User.create(
            id = id,
            username = username,
            email = email,
            profileImageUrl = profileImageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }
}
