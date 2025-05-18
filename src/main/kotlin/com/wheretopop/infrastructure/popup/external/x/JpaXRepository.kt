package com.wheretopop.infrastructure.popup.external.x

import com.wheretopop.shared.infrastructure.entity.XEntity
import com.wheretopop.shared.infrastructure.entity.XId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA X 저장소
 */
@Repository
interface JpaXRepository : JpaRepository<XEntity, XId>

/**
 * X 저장소 JPA 구현체
 */
@Repository
class JpaXRepositoryImpl(
    private val jpaRepository: JpaXRepository
) : XRepository {
    
    override fun save(entity: XEntity): XEntity =
        jpaRepository.save(entity)

    override fun save(entities: List<XEntity>): List<XEntity> =
        jpaRepository.saveAll(entities)
}

