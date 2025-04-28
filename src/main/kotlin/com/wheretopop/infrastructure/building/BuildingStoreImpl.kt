package com.wheretopop.infrastructure.building

import com.wheretopop.domain.building.Building
import com.wheretopop.domain.building.BuildingStore
import org.springframework.stereotype.Component

/**
 * BuildingStore 인터페이스 구현체
 * 도메인 레이어와 인프라 레이어를 연결하는 역할을 담당
 */
@Component
class BuildingStoreImpl(
    private val buildingRepository: BuildingRepository
) : BuildingStore {

    override suspend fun save(building: Building): Building {
        return buildingRepository.save(building)
    }
    override suspend fun save(buildings: List<Building>): List<Building> {
        return buildingRepository.save(buildings)
    }
    override suspend fun delete(building: Building) {
        buildingRepository.deleteById(building.id)
    }   
    override suspend fun delete(buildings: List<Building>) {
        buildings.forEach { building ->
            buildingRepository.deleteById(building.id)
        }
    }
} 