package com.wheretopop.domain.building

interface BuildingService {
    fun getBuilding(address: String): BuildingInfo.Main?
    fun searchBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<BuildingInfo.Main>
    fun createBuilding(command: BuildingCommand.CreateBuildingCommand): BuildingInfo.Main?
}