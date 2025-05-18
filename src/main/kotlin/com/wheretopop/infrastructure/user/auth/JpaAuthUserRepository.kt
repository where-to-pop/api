package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthUser
import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.shared.infrastructure.entity.AuthUserEntity
import mu.KotlinLogging
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

/**
 * JPA 인증 사용자 저장소 인터페이스
 */
@Repository
interface JpaAuthUserRepository : JpaRepository<AuthUserEntity, AuthUserId> {
    fun findByUserId(userId: UserId): AuthUserEntity?
    fun findByIdentifier(identifier: String): AuthUserEntity?
}

/**
 * 인증 사용자 저장소 JPA 구현체
 */
@Repository
class JpaAuthUserRepositoryImpl(
    private val jpaRepository: JpaAuthUserRepository
) : AuthUserRepository {

    override fun findById(id: AuthUserId): AuthUser? {
        val authUser = jpaRepository.findById(id).orElse(null)?.toDomain()
        logger.debug { "findById: $id -> $authUser" }
        return authUser
    }

    override fun findByUserId(userId: UserId): AuthUser? {
        val authUser = jpaRepository.findByUserId(userId)?.toDomain()
        logger.debug { "findByUserId: $userId -> $authUser" }
        return authUser
    }

    override fun findByIdentifier(identifier: String): AuthUser? {
        val authUser = jpaRepository.findByIdentifier(identifier)?.toDomain()
        logger.debug { "findByIdentifier: $identifier -> $authUser" }
        return authUser
    }

    override fun save(authUser: AuthUser): AuthUser {
        val authUserEntity = AuthUserEntity.of(authUser)
        val savedEntity = jpaRepository.save(authUserEntity)
        return savedEntity.toDomain()
    }

    override fun save(authUsers: List<AuthUser>): List<AuthUser> {
        val authUserEntities = authUsers.map { AuthUserEntity.of(it) }
        val savedEntities = jpaRepository.saveAll(authUserEntities)
        return savedEntities.map { it.toDomain() }
    }

    override fun deleteById(id: AuthUserId) {
        jpaRepository.deleteById(id)
    }
} 