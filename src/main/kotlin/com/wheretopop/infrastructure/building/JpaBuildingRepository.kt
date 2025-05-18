package com.wheretopop.infrastructure.building

import com.wheretopop.domain.building.Building
import com.wheretopop.domain.building.BuildingId
import com.wheretopop.shared.infrastructure.entity.BuildingEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
@Transactional
class JpaBuildingRepository(
    @PersistenceContext private val entityManager: EntityManager
) : BuildingRepository {

    override fun findById(id: BuildingId): Building? =
        entityManager.find(BuildingEntity::class.java, id)?.toDomain()

    override fun findByName(name: String): Building? {
        val query = entityManager.createQuery(
            "SELECT b FROM BuildingEntity b WHERE b.name = :name AND b.deletedAt IS NULL", 
            BuildingEntity::class.java
        )
        query.setParameter("name", name)
        
        return query.resultList.firstOrNull()?.toDomain()
    }

    override fun findByAddress(address: String): Building? {
        val query = entityManager.createQuery(
            "SELECT b FROM BuildingEntity b WHERE b.address = :address AND b.deletedAt IS NULL", 
            BuildingEntity::class.java
        )
        query.setParameter("address", address)
        
        return query.resultList.firstOrNull()?.toDomain()
    }

    override fun findAll(): List<Building> {
        val query = entityManager.createQuery(
            "SELECT b FROM BuildingEntity b WHERE b.deletedAt IS NULL", 
            BuildingEntity::class.java
        )
        
        return query.resultList.map { it.toDomain() }
    }

    override fun save(building: Building): Building {
        val entity = BuildingEntity.of(building)
        entityManager.persist(entity)
        return building
    }

    override fun save(buildings: List<Building>): List<Building> =
        buildings.map { save(it) }

}

