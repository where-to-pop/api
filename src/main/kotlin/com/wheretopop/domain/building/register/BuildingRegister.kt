package com.wheretopop.domain.building.register

import com.wheretopop.domain.building.BuildingId
import com.wheretopop.shared.model.Location
import java.time.Instant

class BuildingRegister private constructor(
    val id: BuildingRegisterId,
    val buildingId: BuildingId,
    val address: String,
    var location: Location,
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
    var createdAt: Instant,
    var updatedAt: Instant,
    var deletedAt: Instant? = null,
) {
    companion object {
        fun create(
            id: BuildingRegisterId = BuildingRegisterId.create(),
            buildingId: BuildingId,
            address: String,
            location: Location,
            heit: Double? = null,
            grndFlrCnt: Int? = null,
            ugrndFlrCnt: Int? = null,
            rideUseElvtCnt: Int? = null,
            emgenUseElvtCnt: Int? = null,
            useAprDay: Instant? = null,
            bldNm: String? = null,
            platArea: Double? = null,
            archArea: Double? = null,
            bcRat: Double? = null,
            valRat: Double? = null,
            totArea: Double? = null,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant?,
        ): BuildingRegister {
            return BuildingRegister(
                id = id,
                buildingId = buildingId,
                address = address,
                location = location,
                heit = heit,
                grndFlrCnt = grndFlrCnt,
                ugrndFlrCnt = ugrndFlrCnt,
                rideUseElvtCnt = rideUseElvtCnt,
                emgenUseElvtCnt = emgenUseElvtCnt,
                useAprDay = useAprDay,
                bldNm = bldNm,
                platArea = platArea,
                archArea = archArea,
                bcRat = bcRat,
                valRat = valRat,
                totArea = totArea,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }

    /**
     * 권역의 위치 정보 업데이트
     * @param newLocation 새 위치 정보
     * @return 업데이트된 Area 객체
     */
    fun updateLocation(newLocation: Location): BuildingRegister {
        this.location = newLocation
        this.updatedAt = Instant.now()
        return this
    }
}
