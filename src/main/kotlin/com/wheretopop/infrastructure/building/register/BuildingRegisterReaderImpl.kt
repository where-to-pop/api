package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterCriteria
import com.wheretopop.domain.building.register.BuildingRegisterId
import com.wheretopop.domain.building.register.BuildingRegisterReader
import org.springframework.stereotype.Component

/**
 * BuildingRegisterReader 인터페이스의 구현체
 * Repository 패턴을 통해 조회를 위임합니다.
 */
@Component
class BuildingRegisterReaderImpl(
    private val buildingRegisterRepository: BuildingRegisterRepository
) : BuildingRegisterReader {

    override suspend fun findById(id: BuildingRegisterId): BuildingRegister? {
        return buildingRegisterRepository.findById(id)
    }
    
    override suspend fun findByName(name: String): BuildingRegister? {
        return buildingRegisterRepository.findByName(name)
    }

    override suspend fun findBuildingRegisters(criteria: BuildingRegisterCriteria.SearchBuildingRegisterCriteria): List<BuildingRegister> {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): List<BuildingRegister> {
        return buildingRegisterRepository.findAll()
    }
}