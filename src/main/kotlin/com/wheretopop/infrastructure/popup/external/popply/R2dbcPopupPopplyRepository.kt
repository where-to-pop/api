package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Query.query

internal class R2dbcPopupPopplyRepository (
    private val entityTemplate: R2dbcEntityTemplate
) : PopupPopplyRepository {

    private val entityClass = PopupPopplyEntity::class.java

    override suspend fun save(entity: PopupPopplyEntity): PopupPopplyEntity {
        val existing = loadEntityById(entity.id)
        return if (existing == null) {
            entityTemplate.insert(entity).awaitSingle()
        } else {
            entityTemplate.update(entity).awaitSingle()
        }
    }

    override suspend fun save(entities: List<PopupPopplyEntity>): List<PopupPopplyEntity> =
        entities.map { save(it) }

    override suspend fun findAll(): List<PopupInfo> =
        entityTemplate.select(Query.query(Criteria.empty())
            .sort(Sort.by(Sort.Direction.DESC, "createdAt")), entityClass)
            .map(PopupPopplyEntity::toDomain)
            .collectList()
            .awaitSingle()

    override suspend fun findByPopupId(popupId: PopupId): PopupPopplyEntity? =
        entityTemplate
            .selectOne(query(where("popup_id").`is`(popupId)), entityClass)
            .awaitSingleOrNull()

    override suspend fun findByPopplyId(popplyId: Int): PopupPopplyEntity? =
        entityTemplate
            .selectOne(query(where("popply_id").`is`(popplyId)), entityClass)
            .awaitSingleOrNull()

    private suspend fun loadEntityById(id: PopupPopplyId): PopupPopplyEntity? =
        entityTemplate
            .selectOne(query(where("id").`is`(id)), entityClass)
            .awaitSingleOrNull()

}