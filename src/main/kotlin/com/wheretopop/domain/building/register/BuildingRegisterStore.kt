package com.wheretopop.domain.building.register

interface BuildingRegisterStore {
    fun callAndSave(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegister?
    fun callAndSave(commands: List<BuildingRegisterCommand.CreateBuildingRegisterCommand>): List<BuildingRegister?>
}