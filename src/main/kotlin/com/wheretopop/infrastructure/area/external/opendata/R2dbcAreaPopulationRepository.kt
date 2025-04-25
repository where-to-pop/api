package com.wheretopop.infrastructure.area.external.opendata

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

internal class R2dbcAreaPopulationRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : AreaPopulationRepository {

    private val entityClass = AreaPopulationEntity::class.java

    override suspend fun save(entity: AreaPopulationEntity): AreaPopulationEntity {
        val existing = loadEntityById(entity.id)
        return if (existing == null) {
            entityTemplate.insert(entity).awaitSingle()
        } else {
            entityTemplate.update(entity).awaitSingle()
        }
    }

    override suspend fun save(entities: List<AreaPopulationEntity>): List<AreaPopulationEntity> =
        entities.map { save(it) }


    private suspend fun loadEntityById(id: AreaPopulationId): AreaPopulationEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()
}

