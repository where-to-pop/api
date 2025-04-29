package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterStore
import org.springframework.stereotype.Component

/**
 * BuildingRegisterStore 인터페이스 구현체
 * 도메인 레이어와 인프라 레이어를 연결하는 역할을 담당
 */
@Component
class BuildingRegisterStoreImpl(
    private val buildingRegisterRepository: BuildingRegisterRepository
) : BuildingRegisterStore {

    override suspend fun save(buildingRegister: BuildingRegister): BuildingRegister {
        return buildingRegisterRepository.save(buildingRegister)
    }
    override suspend fun save(buildingRegisters: List<BuildingRegister>): List<BuildingRegister> {
        return buildingRegisterRepository.save(buildingRegisters)
    }
    override suspend fun delete(buildingRegister: BuildingRegister) {
        buildingRegisterRepository.deleteById(buildingRegister.id)
    }   
    override suspend fun delete(buildingRegisters: List<BuildingRegister>) {
        buildingRegisters.forEach { buildingRegister ->
            buildingRegisterRepository.deleteById(buildingRegister.id)
        }
    }
} 