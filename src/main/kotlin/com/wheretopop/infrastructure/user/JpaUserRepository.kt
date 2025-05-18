package com.wheretopop.infrastructure.user

import com.wheretopop.domain.user.User
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.infrastructure.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA 사용자 저장소 인터페이스
 */
@Repository
interface JpaUserRepository : JpaRepository<UserEntity, UserId> {
    fun findByEmail(email: String): UserEntity?
}

/**
 * 사용자 저장소 JPA 구현체
 */
@Repository
class UserRepositoryJpaAdapter(
    private val jpaRepository: JpaUserRepository
) : UserRepository {

    override fun findById(id: UserId): User? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email)?.toDomain()
    }

    override fun save(user: User): User {
        val userEntity = UserEntity.of(user)
        val savedEntity = jpaRepository.save(userEntity)
        return savedEntity.toDomain()
    }
} 