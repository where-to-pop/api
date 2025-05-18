package com.wheretopop.infrastructure.popup.external.x

import com.wheretopop.shared.infrastructure.entity.XEntity

interface XRepository {
    fun save(entity: XEntity): XEntity
    fun save(entities: List<XEntity>): List<XEntity>
}