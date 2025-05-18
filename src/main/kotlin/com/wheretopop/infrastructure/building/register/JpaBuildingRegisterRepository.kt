package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterId
import com.wheretopop.domain.building.BuildingId
import com.wheretopop.shared.infrastructure.entity.BuildingRegisterEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


/**
 * JPA 빌딩 등록정보 저장소 인터페이스
 */
@Repository
interface JpaBuildingRegisterRepository : JpaRepository<BuildingRegisterEntity, Long> {
    
    @Query("SELECT b FROM BuildingRegisterEntity b WHERE b.deletedAt IS NULL")
    override fun findAll(): List<BuildingRegisterEntity>

    @Query("SELECT b FROM BuildingRegisterEntity b WHERE b.buildingId = :buildingId AND b.deletedAt IS NULL")
    fun findByBuildingId(@Param("buildingId") buildingId: Long): BuildingRegisterEntity?
}

/**
 * 빌딩 등록정보 저장소 JPA 구현체
 */
@Repository
class BuildingRegisterRepositoryJpaAdapter(
    private val jpaRepository: JpaBuildingRegisterRepository
) : BuildingRegisterRepository {

    override fun findById(id: BuildingRegisterId): BuildingRegister? =
        jpaRepository.findById(id.toLong()).orElse(null)?.toDomain()

    override fun findByBuildingId(buildingId: BuildingId): BuildingRegister? =
        jpaRepository.findByBuildingId(buildingId.toLong())?.toDomain()


    override fun findAll(): List<BuildingRegister> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun save(building: BuildingRegister): BuildingRegister {
        val entity = BuildingRegisterEntity.of(building)
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun save(buildings: List<BuildingRegister>): List<BuildingRegister> =
        buildings.map { save(it) }
}

