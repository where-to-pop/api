package com.wheretopop.domain.building
import com.wheretopop.domain.building.register.BuildingRegisterCommand
import com.wheretopop.domain.building.register.BuildingRegisterService
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BuildingServiceImpl(
    private val buildingReader: BuildingReader,
    private val buildingStore: BuildingStore,
    private val buildingRegisterService: BuildingRegisterService
) : BuildingService {

    private val buildingInfoMapper = BuildingInfoMapper()

    override suspend fun getBuilding(address: String): BuildingInfo.Main? {
        val building = buildingReader.findByAddress(address) ?: return null
        return buildingInfoMapper.of(building)
    }

    override suspend fun searchBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<BuildingInfo.Main> {
        val buildings = this.buildingReader.findBuildings(criteria)
        return buildingInfoMapper.of(buildings)
    }

    override suspend fun createBuilding(command: BuildingCommand.CreateBuildingCommand): BuildingInfo.Main? {
        val building = Building.create(
            address = command.address,
            location = command.location,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null,
        )
        this.buildingStore.save(building)

        val createBuildingRegisterCommand = BuildingRegisterCommand.CreateBuildingRegisterCommand(
            buildingId = building.id,
            location = building.location,
            address = building.address
        )
        buildingRegisterService.createBuildingRegister(createBuildingRegisterCommand)

        return buildingInfoMapper.of(building)
    }

    override suspend fun getOrCreateBuildingId(command: BuildingCommand.CreateBuildingCommand): Long {
        val targetBuilding = this.getBuilding(command.address)
        if (targetBuilding == null) {
            val building = this.createBuilding(command)
            if (building == null) throw Exception("빌딩 생성에 실패했습니다.")
            return building.id
        } else {
            return targetBuilding.id
        }
    }
}
