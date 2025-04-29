package com.wheretopop.domain.building.register

import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BuildingRegisterServiceImpl(
    private val buildingRegisterReader: BuildingRegisterReader,
    private val buildingRegisterStore: BuildingRegisterStore,
) : BuildingRegisterService {

    private val buildingRegisterInfoMapper = BuildingRegisterInfoMapper()

    override suspend fun searchBuildingRegisters(criteria: BuildingRegisterCriteria.SearchBuildingRegisterCriteria): List<BuildingRegisterInfo.Main> {
        val buildingRegisters = this.buildingRegisterReader.findBuildingRegisters(criteria)
        return buildingRegisterInfoMapper.of(buildingRegisters)
    }

    override suspend fun createBuildingRegister(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegisterInfo.Main? {
        val buildingRegister = this.buildingRegisterStore.callAndSave(command) ?: return null
        return buildingRegisterInfoMapper.of(buildingRegister)
    }
}
