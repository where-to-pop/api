package com.wheretopop.infrastructure.user

import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.infrastructure.entity.UserEntity
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository

@Repository
internal class R2dbcUserRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : UserRepository {

    private val userEntityClass = UserEntity::class.java

    override suspend fun findById(id: UserId): User? {
        return entityTemplate
            .selectOne(query(where("id").`is`(id)), userEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun findByEmail(email: String): User? {
        return entityTemplate
            .selectOne(query(where("email").`is`(email)), userEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun save(user: User): User {
        val userEntity = UserEntity.of(user)
        val exists = entityTemplate.exists(query(where("id").`is`(user.id)), userEntityClass).awaitSingle()
        
        if (exists) {
            entityTemplate.update(userEntity).awaitSingle()
        } else {
            entityTemplate.insert(userEntity).awaitSingle()
        }
        
        return user
    }
} 