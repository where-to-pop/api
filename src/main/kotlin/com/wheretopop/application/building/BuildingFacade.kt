package com.wheretopop.application.building

import com.wheretopop.domain.building.BuildingCommand
import com.wheretopop.domain.building.BuildingCriteria
import com.wheretopop.domain.building.BuildingInfo
import com.wheretopop.domain.building.BuildingService
import com.wheretopop.domain.building.register.BuildingRegisterService
import com.wheretopop.shared.response.ErrorCode
import com.wheretopop.shared.exception.toException
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

    fun findBuildingByAddress(address: String): BuildingOutput.BuildingDetail {
        val building = buildingService.findBuildingByAddress(address)
        val buildingRegister = buildingRegisterService.findBuildingRegisterByBuildingId(building.id) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        return BuildingOutput.BuildingDetail(
            id = building.id,
            latitude = building.location.latitude,
            longitude = building.location.longitude,
            address = building.address,
            height = buildingRegister.heit,
            groundFloorCount = buildingRegister.grndFlrCnt,
            undergroundFloorCount = buildingRegister.ugrndFlrCnt,
            rideUseElevatorCount = buildingRegister.rideUseElvtCnt,
            emergencyUseElevatorCount = buildingRegister.emgenUseElvtCnt,
            useApprovalDay = buildingRegister.useAprDay,
            buildingName = buildingRegister.bldNm,
            plotArea = buildingRegister.platArea,
            architectureArea = buildingRegister.archArea,
            buildingCoverageRatio = buildingRegister.bcRat,
            floorAreaRatio = buildingRegister.valRat,
            totalArea = buildingRegister.totArea,
        )
    }

    fun getOrCreateBuildingId(command: BuildingCommand.CreateBuildingCommand): Long {
        val targetBuilding = buildingService.getBuilding(command.address)
        if (targetBuilding == null) {
            val building = this.createBuilding(command)
            if (building == null) throw Exception("빌딩 생성에 실패했습니다.")
            return building.id.value
        } else {
            return targetBuilding.id.value
        }
    }
}