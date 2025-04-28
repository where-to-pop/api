package com.wheretopop.domain.building

import com.wheretopop.shared.model.Location

class BuildingCommand {
    data class CreateBuildingCommand(
        val address: String,
        val location: Location
    )
}