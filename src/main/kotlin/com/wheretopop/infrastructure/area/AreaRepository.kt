package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaId

interface AreaRepository {
    suspend fun findById(id: AreaId): Area?
    suspend fun findByName(name: String): Area?
    suspend fun findAll(): List<Area>
    suspend fun save(area: Area): Area
    suspend fun save(areas: List<Area>): List<Area>
    suspend fun deleteById(id: AreaId)
}
