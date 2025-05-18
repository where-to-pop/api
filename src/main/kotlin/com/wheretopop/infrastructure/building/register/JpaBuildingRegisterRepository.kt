package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterId
import com.wheretopop.shared.infrastructure.entity.BuildingRegisterEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
@Transactional
class JpaBuildingRegisterRepository(
    @PersistenceContext private val entityManager: EntityManager
) : BuildingRegisterRepository {

    override fun findById(id: BuildingRegisterId): BuildingRegister? =
        entityManager.find(BuildingRegisterEntity::class.java, id)?.toDomain()

    override fun findByName(name: String): BuildingRegister? {
        val query = entityManager.createQuery(
            "SELECT b FROM BuildingRegisterEntity b WHERE b.name = :name AND b.deletedAt IS NULL", 
            BuildingRegisterEntity::class.java
        )
        query.setParameter("name", name)
        
        return query.resultList.firstOrNull()?.toDomain()
    }

    override fun findAll(): List<BuildingRegister> {
        val query = entityManager.createQuery(
            "SELECT b FROM BuildingRegisterEntity b WHERE b.deletedAt IS NULL", 
            BuildingRegisterEntity::class.java
        )
        
        return query.resultList.map { it.toDomain() }
    }

    override fun save(building: BuildingRegister): BuildingRegister {
        val entity = BuildingRegisterEntity.of(building)
        entityManager.persist(entity)
        return building
    }

    override fun save(buildings: List<BuildingRegister>): List<BuildingRegister> =
        buildings.map { save(it) }

}

