package com.wheretopop.domain.building.register

import com.wheretopop.domain.building.BuildingId

interface BuildingRegisterService {
    fun searchBuildingRegisters(criteria: BuildingRegisterCriteria.SearchBuildingRegisterCriteria): List<BuildingRegisterInfo.Main>
    fun createBuildingRegister(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegisterInfo.Main?
    fun findBuildingRegisterByBuildingId(buildingId: BuildingId): BuildingRegisterInfo.Main?
}