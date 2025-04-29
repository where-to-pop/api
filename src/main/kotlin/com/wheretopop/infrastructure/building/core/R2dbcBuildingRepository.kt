package com.wheretopop.infrastructure.building.core

import com.wheretopop.domain.building.core.Building
import com.wheretopop.domain.building.core.BuildingId
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

internal class R2dbcBuildingRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : BuildingRepository {

    private val entityClass = BuildingEntity::class.java

    override suspend fun findById(id: BuildingId): Building? =
        loadEntityById(id)?.toDomain()

    override suspend fun findByName(name: String): Building? =
        entityTemplate
            .selectOne(query(where("name").`is`(name)), entityClass)
            .awaitSingleOrNull()
            ?.toDomain()

    override suspend fun findAll(): List<Building> =
        entityTemplate.select(entityClass)
            .all()
            .map { it.toDomain() }
            .collectList()
            .awaitSingle()

    override suspend fun save(building: Building): Building {
        val existing = loadEntityById(building.id)
        return if (existing == null) {
            entityTemplate.insert(BuildingEntity.of(building)).awaitSingle()
            building
        } else {
            entityTemplate.update(existing.update(building)).awaitSingle()
            building
        }
    }

    override suspend fun save(buildings: List<Building>): List<Building> =
        buildings.map { save(it) }

    override suspend fun deleteById(id: BuildingId) {
        loadEntityById(id)?.let { entityTemplate.delete(it).awaitSingle() }
    }

    private suspend fun loadEntityById(id: BuildingId): BuildingEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()
}

