package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaId
import com.wheretopop.shared.infrastructure.entity.AreaEntity
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

internal class R2dbcAreaRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : AreaRepository {

    private val entityClass = AreaEntity::class.java

    override suspend fun findById(id: AreaId): Area? =
        loadEntityById(id)?.toDomain()

    override suspend fun findByName(name: String): Area? =
        entityTemplate
            .selectOne(query(where("name").`is`(name)), entityClass)
            .awaitSingleOrNull()
            ?.toDomain()

    override suspend fun findAll(): List<Area> =
        entityTemplate.select(entityClass)
            .all()
            .map { it.toDomain() }
            .collectList()
            .awaitSingle()

    override suspend fun save(area: Area): Area {
        val existing = loadEntityById(area.id)
        return if (existing == null) {
            entityTemplate.insert(AreaEntity.of(area)).awaitSingle()
            area
        } else {
            entityTemplate.update(existing.update(area)).awaitSingle()
            area
        }
    }

    override suspend fun save(areas: List<Area>): List<Area> =
        areas.map { save(it) }

    override suspend fun deleteById(id: AreaId) {
        loadEntityById(id)?.let { entityTemplate.delete(it).awaitSingle() }
    }

    private suspend fun loadEntityById(id: AreaId): AreaEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()
}

