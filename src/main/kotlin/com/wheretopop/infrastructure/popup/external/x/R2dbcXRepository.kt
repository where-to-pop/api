package com.wheretopop.infrastructure.popup.external.x

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

internal class R2dbcXRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : XRepository {

    private val entityClass = XEntity::class.java


    override suspend fun save(entity: XEntity): XEntity {
        val existing = loadEntityById(entity.id)
        return if (existing == null) {
            entityTemplate.insert(entity).awaitSingle()
        } else {
            entityTemplate.update(entity).awaitSingle()
        }
    }

    override suspend fun save(entities: List<XEntity>): List<XEntity> =
        entities.map { save(it) }


    private suspend fun loadEntityById(id: XId): XEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()
}

