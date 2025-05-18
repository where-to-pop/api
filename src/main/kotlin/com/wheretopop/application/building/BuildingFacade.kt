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
    fun searchBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<BuildingInfo.Main> {
        return buildingService.searchBuildings(criteria)
    }

    fun createBuilding(command: BuildingCommand.CreateBuildingCommand): BuildingInfo.Main? {
        val building = buildingService.createBuilding(command)
        return building
    }

    fun getOrCreateBuildingId(command: BuildingCommand.CreateBuildingCommand): Long {
        val targetBuilding = buildingService.getBuilding(command.address)
        if (targetBuilding == null) {
            val building = this.createBuilding(command)
            if (building == null) throw Exception("빌딩 생성에 실패했습니다.")
            return building.id
        } else {
            return targetBuilding.id
        }
    }
}