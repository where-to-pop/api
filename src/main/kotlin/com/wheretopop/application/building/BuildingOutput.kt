

import com.wheretopop.domain.building.BuildingId
import com.wheretopop.domain.building.BuildingInfo
import com.wheretopop.domain.building.register.BuildingRegisterInfo
import java.time.Instant

class BuildingOutput {
    data class BuildingDetail (
        val id: BuildingId,
        val latitude: Double,
        val longitude: Double,
        val address: String,
        val height: Double? = null,
        val groundFloorCount: Int? = null,
        val undergroundFloorCount: Int? = null,
        val rideUseElevatorCount: Int? = null,
        val emergencyUseElevatorCount: Int? = null,
        val useApprovalDay: Instant? = null,
        val buildingName: String? = null,
        val plotArea: Double? = null,
        val architectureArea: Double? = null,
        val buildingCoverageRatio: Double? = null,
        val floorAreaRatio: Double? = null,
        val totalArea: Double? = null,
    )
}