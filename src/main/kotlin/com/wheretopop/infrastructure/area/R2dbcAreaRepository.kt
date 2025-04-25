package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.model.UniqueId
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component
import java.util.*


@Component
internal class R2dbcAreaRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : AreaRepository {

    private val entityClass = AreaEntity::class.java

    override fun findById(id: UniqueId): Optional<Area> {
        return Optional.ofNullable(loadById(id)?.toDomain())
    }

    override fun findByName(name: String): Optional<Area> {
        return Optional.ofNullable(
            entityTemplate
                .selectOne(query(where("name").`is`(name)), entityClass)
                .block()
                ?.toDomain()
        )
    }

    override fun findByRegionId(regionId: Long): List<Area> {
        return entityTemplate
            .select(query(where("region_id").`is`(regionId)), entityClass)
            .map { it.toDomain() }
            .collectList()
            .block() ?: emptyList()
    }

    override fun findByLocation(latitude: Double, longitude: Double, radiusKm: Double): List<Area> {
        // TODO: 위치 기반 쿼리 구현
        return emptyList()
    }

    override fun save(area: Area): Area {
        val entity = loadById(area.id)
        if (entity == null) {
            entityTemplate.insert(AreaEntity.of(area)).block()
        } else {
            entityTemplate.update(entity.update(area)).block()
        }
        return area
    }

    override fun deleteById(id: UniqueId) {
        loadById(id)?.run {
            entityTemplate.delete(this).block()
        }
    }

    private fun loadById(id: UniqueId) = entityTemplate
        .selectOne(query(where("id").`is`(id.toLong())), entityClass)
        .block()
} 