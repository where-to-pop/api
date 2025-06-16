package com.wheretopop.interfaces.building

import com.wheretopop.application.building.BuildingFacade
import com.wheretopop.domain.building.BuildingId
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class BuildingToolRegistryTest {
    private lateinit var buildingFacade: BuildingFacade
    private lateinit var registry: BuildingToolRegistry

    @BeforeEach
    fun setUp() {
        buildingFacade = mockk()
        registry = BuildingToolRegistry(buildingFacade)
    }

    @Test
    @DisplayName("주소로 건물 정보를 조회하면 포맷된 문자열을 반환한다")
    fun findBuildingByAddress() {
        val detail = BuildingOutput.BuildingDetail(
            id = BuildingId.of(1L),
            latitude = 1.0,
            longitude = 2.0,
            address = "addr",
            height = 10.0,
            groundFloorCount = 5,
            undergroundFloorCount = 1,
            rideUseElevatorCount = 2,
            emergencyUseElevatorCount = 1
        )
        every { buildingFacade.findBuildingByAddress("addr") } returns detail

        val result = registry.findBuildingByAddress("addr")

        assertTrue(result.contains("Building Information for"))
        assertTrue(result.contains("addr"))
    }
}
