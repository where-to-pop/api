package com.wheretopop.application.building

import com.wheretopop.domain.building.BuildingCommand
import com.wheretopop.domain.building.BuildingId
import com.wheretopop.domain.building.BuildingInfo
import com.wheretopop.domain.building.BuildingService
import com.wheretopop.domain.building.register.BuildingRegisterService
import com.wheretopop.shared.model.Location
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildingFacadeTest {
    private lateinit var buildingService: BuildingService
    private lateinit var buildingRegisterService: BuildingRegisterService
    private lateinit var buildingFacade: BuildingFacade

    @BeforeEach
    fun setUp() {
        buildingService = mockk()
        buildingRegisterService = mockk()
        buildingFacade = BuildingFacade(buildingService, buildingRegisterService)
    }

    @Test
    @DisplayName("이미 존재하는 건물 ID를 반환한다")
    fun getExistingBuildingId() {
        val command = BuildingCommand.CreateBuildingCommand("addr", Location(1.0, 2.0))
        val existing = BuildingInfo.Main(BuildingId.of(1L), "addr", BuildingInfo.LocationInfo(1.0, 2.0))
        every { buildingService.getBuilding("addr") } returns existing

        val id = buildingFacade.getOrCreateBuildingId(command)

        assertEquals(1L, id)
        verify(exactly = 0) { buildingService.createBuilding(any()) }
    }

    @Test
    @DisplayName("건물이 없으면 새로 생성하여 ID를 반환한다")
    fun createAndReturnBuildingId() {
        val command = BuildingCommand.CreateBuildingCommand("addr", Location(1.0, 2.0))
        every { buildingService.getBuilding("addr") } returns null
        val created = BuildingInfo.Main(BuildingId.of(2L), "addr", BuildingInfo.LocationInfo(1.0, 2.0))
        every { buildingService.createBuilding(command) } returns created

        val id = buildingFacade.getOrCreateBuildingId(command)

        assertEquals(2L, id)
        verify { buildingService.createBuilding(command) }
    }
}
