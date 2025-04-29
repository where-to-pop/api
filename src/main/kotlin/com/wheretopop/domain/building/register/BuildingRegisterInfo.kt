package com.wheretopop.domain.building.register


class BuildingRegisterInfo {

    /**
     * Building 기본 정보를 담은 DTO
     */
    data class Main(
        val id: Long,
        val address: String,
        val location: LocationInfo,
    )
    
    /**
     * Location 정보를 담은 DTO
     */
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double
    )
}