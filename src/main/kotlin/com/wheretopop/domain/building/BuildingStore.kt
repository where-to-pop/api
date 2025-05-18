package com.wheretopop.domain.building

interface BuildingStore {
    fun save(building: Building): Building
    fun save(buildings: List<Building>): List<Building>
}