package com.wheretopop.infrastructure.user

import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.model.UniqueId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("users")
internal class UserEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: UserId,
    @Column("username")
    val username: String,
    @Column("email")
    val email: String,
    @Column("profile_image_url")
    val profileImageUrl: String?,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant?
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

@WritingConverter
class UserIdToLongConverter : Converter<UserId, Long> {
    override fun convert(source: UserId) = source.toLong()
}

@ReadingConverter
class LongToUserIdConverter : Converter<Long, UserId> {
    override fun convert(source: Long) = UserId.of(source)
}
