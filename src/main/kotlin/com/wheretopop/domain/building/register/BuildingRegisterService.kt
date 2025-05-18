package com.wheretopop.domain.building.register

interface BuildingRegisterService {
    fun searchBuildingRegisters(criteria: BuildingRegisterCriteria.SearchBuildingRegisterCriteria): List<BuildingRegisterInfo.Main>
    fun createBuildingRegister(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegisterInfo.Main?
}