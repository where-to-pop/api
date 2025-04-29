package com.wheretopop.domain.building.register

import com.wheretopop.domain.building.BuildingCriteria
import com.wheretopop.domain.building.BuildingInfo
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

    override suspend fun createBuildingRegister(command: BuildingRegisterCommand.CreateBuildingRegisterCommand): BuildingRegisterInfo.Main {
        val buildingRegister = this.buildingRegisterStore.save(
            BuildingRegister.create(
                address = command.address,
                location = command.location,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null,
            )
        )
        return buildingRegisterInfoMapper.of(buildingRegister)
    }
}
