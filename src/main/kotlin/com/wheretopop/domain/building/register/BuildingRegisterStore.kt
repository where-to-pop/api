package com.wheretopop.domain.building.register

interface BuildingRegisterStore {
    suspend fun callAndSave(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegister?
    suspend fun callAndSave(commands: List<BuildingRegisterCommand.CreateBuildingRegisterCommand>): List<BuildingRegister?>
    suspend fun delete(buildingRegister: BuildingRegister)
    suspend fun delete(buildingRegisters: List<BuildingRegister>)
}