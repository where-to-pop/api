package com.wheretopop.infrastructure.area.external.opendata

interface AreaPopulationRepository {
    suspend fun save(entity: AreaPopulationEntity): AreaPopulationEntity
    suspend fun save(entities: List<AreaPopulationEntity>): List<AreaPopulationEntity>
}