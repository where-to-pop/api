package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.shared.infrastructure.entity.AreaEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

/**
 * 지역(Area) 리포지토리 JPA 구현체
 */
@Repository
@Transactional
class JpaAreaRepository(
    @PersistenceContext private val entityManager: EntityManager
) : AreaRepository {

    override fun findById(id: AreaId): Area? =
        entityManager.find(AreaEntity::class.java, id)?.toDomain()

    override fun findByName(name: String): Area? {
        val query = entityManager.createQuery(
            "SELECT a FROM AreaEntity a WHERE a.name = :name AND a.deletedAt IS NULL", 
            AreaEntity::class.java
        )
        query.setParameter("name", name)
        
        return query.resultList.firstOrNull()?.toDomain()
    }

    override fun findAll(): List<Area> {
        val query = entityManager.createQuery(
            "SELECT a FROM AreaEntity a WHERE a.deletedAt IS NULL", 
            AreaEntity::class.java
        )
        
        return query.resultList.map { it.toDomain() }
    }

    @Transactional
    override fun save(area: Area): Area {
        val entity = AreaEntity.from(area)
        entityManager.persist(entity)
        return area
    }

    @Transactional
    override fun save(areas: List<Area>): List<Area> =
        areas.map { save(it) }

    @Transactional
    override fun deleteById(id: AreaId) {
        entityManager.find(AreaEntity::class.java, id)?.let { 
            it.deletedAt = java.time.Instant.now()
            entityManager.merge(it)
        }
    }
}

