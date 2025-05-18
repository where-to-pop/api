package com.wheretopop.interfaces.building

import com.wheretopop.application.building.BuildingFacade
import mu.KotlinLogging
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class BuildingToolRegistry(
    private val buildingFacade: BuildingFacade
) {
    private val logger = KotlinLogging.logger {}

    @Tool(description = """
        Retrieves detailed building information by address.
        Use this tool when a user requests specific information about a building at a particular address.
        Appropriate for questions like:
        - 'Tell me about the building at [address]'
        - 'What are the details of [building address]?'
        - 'Show me information for the building located at [address]'
        The response includes:
        - Building ID and location (latitude/longitude)
        - Building specifications (height, floor counts)
        - Elevator information
        - Building usage details
        - Construction/approval information
        Returns an appropriate error message if the building is not found.
    """)
    fun findBuildingByAddress(address: String): String {
        try {
            logger.info("findBuildingByAddress tool was called: address={}", address)

            val building = buildingFacade.findBuildingByAddress(address)

            return """
                |Building Information for: $address
                |
                |Basic Information:
                |• Building ID: ${building.id}
                |• Location: Latitude ${building.latitude}, Longitude ${building.longitude}
                |• Address: ${building.address}
                |
                |Building Specifications:
                |• Height: ${building.height} meters
                |• Ground Floor Count: ${building.groundFloorCount} floors
                |• Underground Floor Count: ${building.undergroundFloorCount} floors
                |
                |Elevator Information:
                |• Passenger Elevators: ${building.rideUseElevatorCount}
                |• Emergency Elevators: ${building.emergencyUseElevatorCount}
                |
                |Building Details:
                |• Building Name: ${building.buildingName}
                |• Plot Area: ${building.plotArea} m²
                |• Architecture Area: ${building.architectureArea} m²
                |• Total Area: ${building.totalArea} m²
                |
                |Building Ratios:
                |• Building Coverage Ratio: ${building.buildingCoverageRatio}%
                |• Floor Area Ratio: ${building.floorAreaRatio}%
                |
                |Approval Information:
                |• Use Approval Date: ${building.useApprovalDay}
            """.trimMargin()

        } catch (e: Exception) {
            logger.error("Error retrieving building information for address: $address", e)
            return "죄송합니다. '$address' 주소의 건물 정보를 찾는 도중 오류가 발생했습니다. 다시 시도해 주세요."
        }
    }
} 