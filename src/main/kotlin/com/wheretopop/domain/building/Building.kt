package com.wheretopop.domain.building

import com.wheretopop.shared.model.Location
import java.time.Instant

class Building private constructor(
    val id: BuildingId,
    val address: String,
    var location: Location,
    val createdAt: Instant,
    var updatedAt: Instant,
    var deletedAt: Instant? = null
) {
    companion object {
        fun create(
            id: BuildingId = BuildingId.create(),
            address: String,
            location: Location,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant?,
        ): Building {
            return Building(
                id = id,
                address = address,
                location = location,
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
    fun updateLocation(newLocation: Location): Building {
        this.location = newLocation
        this.updatedAt = Instant.now()
        return this
    }
}
