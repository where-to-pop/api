package com.wheretopop.infrastructure.user.auth

import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.RefreshToken
import com.wheretopop.domain.user.auth.RefreshTokenId
import com.wheretopop.shared.infrastructure.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA 리프레시 토큰 저장소 인터페이스
 */
@Repository
interface JpaRefreshTokenRepository : JpaRepository<RefreshTokenEntity, RefreshTokenId> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun findByAuthUserId(authUserId: AuthUserId): List<RefreshTokenEntity>
    fun deleteByToken(token: String)
}

/**
 * 리프레시 토큰 저장소 JPA 구현체
 */
@Repository
class RefreshTokenRepositoryJpaAdapter(
    private val jpaRepository: JpaRefreshTokenRepository
) : RefreshTokenRepository {

    override fun findById(id: RefreshTokenId): RefreshToken? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByToken(token: String): RefreshToken? {
        return jpaRepository.findByToken(token)?.toDomain()
    }

    override fun findByUserId(userId: AuthUserId): List<RefreshToken> {
        return jpaRepository.findByAuthUserId(userId).map { it.toDomain() }
    }

    override fun save(refreshToken: RefreshToken): RefreshToken {
        val refreshTokenEntity = RefreshTokenEntity.of(refreshToken)
        val savedEntity = jpaRepository.save(refreshTokenEntity)
        return savedEntity.toDomain()
    }

    override fun deleteById(id: RefreshTokenId) {
        jpaRepository.deleteById(id)
    }

    override fun deleteByToken(token: String) {
        jpaRepository.deleteByToken(token)
    }
} 