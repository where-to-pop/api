package com.wheretopop.application.building

import com.wheretopop.domain.building.BuildingCommand
import com.wheretopop.domain.building.BuildingCriteria
import com.wheretopop.domain.building.BuildingInfo
import com.wheretopop.domain.building.BuildingService
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

    suspend fun createBuilding(command: BuildingCommand.CreateBuildingCommand): BuildingInfo.Main {
        val building = buildingService.createBuilding(command)
        return building
    }
}