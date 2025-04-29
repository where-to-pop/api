package com.wheretopop.domain.building.register

import com.wheretopop.shared.model.Location

class BuildingRegisterCommand {
    data class CreateBuildingRegisterCommand(
        val address: String,
        val location: Location
    )
}