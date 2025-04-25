package com.wheretopop.domain.area

interface AreaStore {
    suspend fun save(area: Area): Area
    suspend fun save(areas: List<Area>): List<Area>
    suspend fun delete(area: Area)
    suspend fun delete(areas: List<Area>)
}