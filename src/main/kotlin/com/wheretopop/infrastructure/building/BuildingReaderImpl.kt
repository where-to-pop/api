package com.wheretopop.infrastructure.building

import com.wheretopop.domain.building.Building
import com.wheretopop.domain.building.BuildingCriteria
import com.wheretopop.domain.building.BuildingId
import com.wheretopop.domain.building.BuildingReader
import org.springframework.stereotype.Component

/**
 * BuildingReader 인터페이스의 구현체
 * Repository 패턴을 통해 조회를 위임합니다.
 */
@Component
class BuildingReaderImpl(
    private val buildingRepository: BuildingRepository
) : BuildingReader {

    override suspend fun findById(id: BuildingId): Building? {
        return buildingRepository.findById(id)
    }
    
    override suspend fun findByName(name: String): Building? {
        return buildingRepository.findByName(name)
    }

    override suspend fun findBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<Building> {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): List<Building> {
        return buildingRepository.findAll()
    }
}