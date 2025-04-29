package com.wheretopop.domain.building

interface BuildingStore {
    suspend fun save(building: Building): Building
    suspend fun save(buildings: List<Building>): List<Building>
    suspend fun delete(building: Building)
    suspend fun delete(buildings: List<Building>)
}