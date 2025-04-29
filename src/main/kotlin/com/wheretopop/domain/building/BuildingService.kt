package com.wheretopop.domain.building

interface BuildingService {
    suspend fun searchBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<BuildingInfo.Main>
    suspend fun createBuilding(command: BuildingCommand.CreateBuildingCommand): BuildingInfo.Main
}