package com.wheretopop.domain.building.register

import com.wheretopop.domain.building.BuildingId
import com.wheretopop.shared.model.Location

class BuildingRegisterCommand {
    data class CreateBuildingRegisterCommand(
        val buildingId: BuildingId,
        val address: String,
        val location: Location
    )
}