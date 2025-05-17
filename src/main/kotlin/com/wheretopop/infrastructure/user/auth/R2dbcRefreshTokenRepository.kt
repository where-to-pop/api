package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.RefreshTokenId
import com.wheretopop.shared.infrastructure.entity.RefreshTokenEntity
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository

@Repository
class R2dbcRefreshTokenRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : RefreshTokenRepository {
    private val refreshTokenEntityClass = RefreshTokenEntity::class.java

    override suspend fun findById(id: RefreshTokenId): RefreshToken? {
        return entityTemplate
            .selectOne(query(where("id").`is`(id)), refreshTokenEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun findByToken(token: String): RefreshToken? {
        return entityTemplate
            .selectOne(query(where("token").`is`(token)), refreshTokenEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun findByUserId(userId: AuthUserId): List<RefreshToken> {
        return entityTemplate
            .select(query(where("auth_user_id").`is`(userId)), refreshTokenEntityClass)
            .collectList()
            .awaitSingle()
            .map { it.toDomain() }
    }

    override suspend fun save(refreshToken: RefreshToken): RefreshToken {
        val refreshTokenEntity = RefreshTokenEntity.of(refreshToken)
        val exists = entityTemplate.exists(query(where("id").`is`(refreshToken.id)), refreshTokenEntityClass).awaitSingle()
        
        if (exists) {
            entityTemplate.update(refreshTokenEntity).awaitSingle()
        } else {
            entityTemplate.insert(refreshTokenEntity).awaitSingle()
        }
        
        return refreshToken
    }

    override suspend fun deleteById(id: RefreshTokenId) {
        entityTemplate
            .delete(query(where("id").`is`(id)), refreshTokenEntityClass)
            .awaitSingle()
    }

    override suspend fun deleteByToken(token: String) {
        entityTemplate
            .delete(query(where("token").`is`(token)), refreshTokenEntityClass)
            .awaitSingle()
    }
} 