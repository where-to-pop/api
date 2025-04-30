package com.wheretopop.domain.building.register

interface BuildingRegisterService {
    suspend fun searchBuildingRegisters(criteria: BuildingRegisterCriteria.SearchBuildingRegisterCriteria): List<BuildingRegisterInfo.Main>
    suspend fun createBuildingRegister(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegisterInfo.Main?
}