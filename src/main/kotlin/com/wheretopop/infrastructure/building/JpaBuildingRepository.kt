package com.wheretopop.infrastructure.building

import com.wheretopop.domain.building.Building
import com.wheretopop.domain.building.BuildingId
import com.wheretopop.shared.infrastructure.entity.BuildingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * JPA 빌딩 저장소 인터페이스
 */
@Repository
interface JpaBuildingRepository : JpaRepository<BuildingEntity, Long> {
    @Query("SELECT b FROM BuildingEntity b WHERE b.address = :address AND b.deletedAt IS NULL")
    fun findByAddress(@Param("address") address: String): BuildingEntity?
    
    @Query("SELECT b FROM BuildingEntity b WHERE b.deletedAt IS NULL")
    override fun findAll(): List<BuildingEntity>
}

/**
 * 빌딩 저장소 JPA 구현체
 */
@Repository
class BuildingRepositoryJpaAdapter(
    private val jpaRepository: JpaBuildingRepository
) : BuildingRepository {

    override fun findById(id: BuildingId): Building? =
        jpaRepository.findById(id.toLong()).orElse(null)?.toDomain()

    override fun findByAddress(address: String): Building? =
        jpaRepository.findByAddress(address)?.toDomain()

    override fun findAll(): List<Building> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun save(building: Building): Building {
        val entity = BuildingEntity.of(building)
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun save(buildings: List<Building>): List<Building> =
        buildings.map { save(it) }
}

