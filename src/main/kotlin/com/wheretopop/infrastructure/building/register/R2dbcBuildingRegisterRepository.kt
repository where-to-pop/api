package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterId
import com.wheretopop.shared.infrastructure.entity.BuildingRegisterEntity
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

internal class R2dbcBuildingRegisterRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : BuildingRegisterRepository {

    private val entityClass = BuildingRegisterEntity::class.java

    override suspend fun findById(id: BuildingRegisterId): BuildingRegister? =
        loadEntityById(id)?.toDomain()

    override suspend fun findByName(name: String): BuildingRegister? =
        entityTemplate
            .selectOne(query(where("name").`is`(name)), entityClass)
            .awaitSingleOrNull()
            ?.toDomain()

    override suspend fun findAll(): List<BuildingRegister> =
        entityTemplate.select(entityClass)
            .all()
            .map { it.toDomain() }
            .collectList()
            .awaitSingle()

    override suspend fun save(building: BuildingRegister): BuildingRegister {
        val existing = loadEntityById(building.id)
        return if (existing == null) {
            entityTemplate.insert(BuildingRegisterEntity.of(building)).awaitSingle()
            building
        } else {
            entityTemplate.update(existing.update(building)).awaitSingle()
            building
        }
    }

    override suspend fun save(buildings: List<BuildingRegister>): List<BuildingRegister> =
        buildings.map { save(it) }

    override suspend fun deleteById(id: BuildingRegisterId) {
        loadEntityById(id)?.let { entityTemplate.delete(it).awaitSingle() }
    }

    private suspend fun loadEntityById(id: BuildingRegisterId): BuildingRegisterEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()
}

