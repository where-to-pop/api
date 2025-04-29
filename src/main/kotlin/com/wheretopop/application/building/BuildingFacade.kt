package com.wheretopop.application.building

import com.wheretopop.domain.building.core.BuildingCommand
import com.wheretopop.domain.building.core.BuildingCriteria
import com.wheretopop.domain.building.core.BuildingInfo
import com.wheretopop.domain.building.core.BuildingService
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