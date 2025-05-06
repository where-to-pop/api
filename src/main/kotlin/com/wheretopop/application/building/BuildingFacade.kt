package com.wheretopop.application.building

import com.wheretopop.domain.building.*
import com.wheretopop.domain.building.register.BuildingRegisterService
import org.springframework.stereotype.Service

@Service
class BuildingFacade(
    private val buildingService: BuildingService,
    private val buildingRegisterService: BuildingRegisterService
) {
    suspend fun searchBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<BuildingInfo.Main> {
        return buildingService.searchBuildings(criteria)
    }

    suspend fun createBuilding(command: BuildingCommand.CreateBuildingCommand): BuildingInfo.Main? {
        val building = buildingService.createBuilding(command)
        return building
    }

    suspend fun getOrCreateBuildingId(command: BuildingCommand.CreateBuildingCommand): Long {
        return buildingService.getOrCreateBuildingId(command)
    }
}