package com.wheretopop.infrastructure.popup.external.x

import com.wheretopop.domain.popup.PopupId

interface XRepository {
    suspend fun save(entity: XEntity): XEntity
    suspend fun save(entities: List<XEntity>): List<XEntity>
}