package com.wheretopop.domain.building.register

interface BuildingRegisterStore {
    suspend fun save(buildingRegister: BuildingRegister): BuildingRegister
    suspend fun save(buildingRegisters: List<BuildingRegister>): List<BuildingRegister>
    suspend fun delete(buildingRegister: BuildingRegister)
    suspend fun delete(buildingRegisters: List<BuildingRegister>)
}