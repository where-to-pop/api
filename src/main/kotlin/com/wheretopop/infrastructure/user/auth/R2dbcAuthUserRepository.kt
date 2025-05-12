package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserId
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository

@Repository
class R2dbcAuthUserRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : AuthUserRepository {
    private val authUserEntityClass = AuthUserEntity::class.java

    override suspend fun findById(id: AuthUserId): AuthUser? {
        return entityTemplate
            .selectOne(query(where("id").`is`(id)), authUserEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun findByUserId(userId: UserId): AuthUser? {
        return entityTemplate
            .selectOne(query(where("user_id").`is`(userId)), authUserEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun findByIdentifier(identifier: String): AuthUser? {
        return entityTemplate
            .selectOne(query(where("identifier").`is`(identifier)), authUserEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun save(authUser: AuthUser): AuthUser {
        val authUserEntity = AuthUserEntity.of(authUser)
        val exists = entityTemplate.exists(query(where("id").`is`(authUser.id)), authUserEntityClass).awaitSingle()
        
        if (exists) {
            entityTemplate.update(authUserEntity).awaitSingle()
        } else {
            entityTemplate.insert(authUserEntity).awaitSingle()
        }
        
        return authUser
    }

    override suspend fun save(authUsers: List<AuthUser>): List<AuthUser> =
        authUsers.map { save(it) }

    override suspend fun deleteById(id: AuthUserId) {
        entityTemplate
            .delete(query(where("id").`is`(id)), authUserEntityClass)
            .awaitSingle()
    }
} 