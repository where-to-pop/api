package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.shared.infrastructure.entity.AreaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

/**
 * JPA 지역 저장소 인터페이스
 */
@Repository
interface JpaAreaRepository : JpaRepository<AreaEntity, Long> {
    @Query("SELECT a FROM AreaEntity a WHERE a.name = :name AND a.deletedAt IS NULL")
    fun findByName(@Param("name") name: String): AreaEntity?
    
    @Query("SELECT a FROM AreaEntity a WHERE a.deletedAt IS NULL")
    override fun findAll(): List<AreaEntity>
}

/**
 * 지역 저장소 JPA 구현체
 */
@Repository
class AreaRepositoryJpaAdapter(
    private val jpaRepository: JpaAreaRepository
) : AreaRepository {

    override fun findById(id: AreaId): Area? =
        jpaRepository.findById(id.toLong()).orElse(null)?.toDomain()

    override fun findByName(name: String): Area? =
        jpaRepository.findByName(name)?.toDomain()

    override fun findAll(): List<Area> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun save(area: Area): Area {
        val entity = AreaEntity.from(area)
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun save(areas: List<Area>): List<Area> =
        areas.map { save(it) }

    override fun deleteById(id: AreaId) {
        jpaRepository.findById(id.toLong()).ifPresent { entity ->
            entity.deletedAt = Instant.now()
            jpaRepository.save(entity)
        }
    }
}

