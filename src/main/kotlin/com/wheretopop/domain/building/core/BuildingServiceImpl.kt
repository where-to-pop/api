package com.wheretopop.domain.building.core
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BuildingServiceImpl(
    private val buildingReader: BuildingReader,
    private val buildingStore: BuildingStore,
) : BuildingService {

    private val buildingInfoMapper = BuildingInfoMapper()

    override suspend fun searchBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<BuildingInfo.Main> {
        val buildings = this.buildingReader.findBuildings(criteria)
        return buildingInfoMapper.of(buildings)
    }

    override suspend fun createBuilding(command: BuildingCommand.CreateBuildingCommand): BuildingInfo.Main {
        val building = this.buildingStore.save(
            Building.create(
                address = command.address,
                location = command.location,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null,
            )
        )
        return buildingInfoMapper.of(building)
    }
}
