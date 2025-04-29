package com.wheretopop.domain.building.register

import java.time.Instant


class BuildingRegisterInfo {

    /**
     * BuildingRegister 기본 정보를 담은 DTO
     */
    data class Main(
        val id: Long,
        val address: String,
        val location: LocationInfo,
        val heit: Double? = null,
        val grndFlrCnt: Int? = null,
        val ugrndFlrCnt: Int? = null,
        val rideUseElvtCnt: Int? = null,
        val emgenUseElvtCnt: Int? = null,
        val useAprDay: Instant? = null,
        val bldNm: String? = null,
        val platArea: Double? = null,
        val archArea: Double? = null,
        val bcRat: Double? = null,
        val valRat: Double? = null,
        val totArea: Double? = null,
    )
    
    /**
     * Location 정보를 담은 DTO
     */
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double
    )
}