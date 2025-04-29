package com.wheretopop.domain.building.core

import com.wheretopop.shared.model.Location

class BuildingCommand {
    data class CreateBuildingCommand(
        val address: String,
        val location: Location
    )
}