package com.wheretopop.infrastructure.area.external.opendata.population

interface AreaPopulationRepository {
    suspend fun save(entity: AreaPopulationEntity): AreaPopulationEntity
    suspend fun save(entities: List<AreaPopulationEntity>): List<AreaPopulationEntity>
}