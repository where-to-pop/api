package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

internal class R2dbcPopupRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : PopupRepository {

    private val entityClass = PopupEntity::class.java

    override suspend fun findById(id: PopupId): Popup? =
        loadEntityById(id)?.toDomain()

    override suspend fun findByName(name: String): Popup? =
        entityTemplate
            .selectOne(query(where("name").`is`(name)), entityClass)
            .awaitSingleOrNull()
            ?.toDomain()

    override suspend fun findAll(): List<Popup> =
        entityTemplate.select(entityClass)
            .all()
            .map { it.toDomain() }
            .collectList()
            .awaitSingle()

    override suspend fun save(popup: Popup): Popup {
        val existing = loadEntityById(popup.id)
        return if (existing == null) {
            entityTemplate.insert(PopupEntity.of(popup)).awaitSingle()
            popup
        } else {
            entityTemplate.update(existing.update(popup)).awaitSingle()
            popup
        }
    }

    override suspend fun save(popups: List<Popup>): List<Popup> =
        popups.map { save(it) }

    override suspend fun deleteById(id: PopupId) {
        loadEntityById(id)?.let { entityTemplate.delete(it).awaitSingle() }
    }

    private suspend fun loadEntityById(id: PopupId): PopupEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()
}