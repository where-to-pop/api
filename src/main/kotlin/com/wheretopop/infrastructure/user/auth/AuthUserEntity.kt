package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.Password
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("auth_users")
internal class AuthUserEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: AuthUserId,
    @Column("user_id")
    val userId: UserId,
    @Column("identifier")
    val identifier: String,
    @Column("password")
    val password : Password,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant?
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

    fun update(authUser: AuthUser): AuthUserEntity {
        return AuthUserEntity(
            id = id,
            userId = userId,
            identifier = authUser.identifier,
            password = authUser.password,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
}

@WritingConverter
class AuthUserIdToLongConverter : Converter<AuthUserId, Long> {
    override fun convert(source: AuthUserId) = source.toLong()
}

@ReadingConverter
class LongToAuthUserIdConverter : Converter<Long, AuthUserId> {
    override fun convert(source: Long) = AuthUserId.of(source)
}

@WritingConverter
class PasswordToStringConverter : Converter<Password, String> {
    override fun convert(source: Password) = source.hashed
}

@ReadingConverter
class StringToPasswordConverter : Converter<String, Password> {
    override fun convert(source: String) = Password.fromHashed(source)
}
