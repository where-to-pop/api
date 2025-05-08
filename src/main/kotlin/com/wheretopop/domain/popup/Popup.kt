package com.wheretopop.domain.popup

import com.wheretopop.domain.building.BuildingId
import java.time.Instant

class Popup private constructor(
    val id: PopupId,
    val buildingId: Long?,
    val name: String,
    val address: String,
    val createdAt: Instant,
    var deletedAt: Instant? = null
) {
    companion object {
        fun create(
            id: PopupId = PopupId.create(),
            buildingId: Long? = null,
            name: String,
            address: String,
            createdAt: Instant = Instant.now(),
            deletedAt: Instant? = null
        ): Popup {
            require(name.isNotBlank()) {"팝업 이름은 필수입니다."}
            return Popup(
                id = id,
                buildingId = buildingId,
                name = name,
                address = address,
                createdAt = createdAt,
                deletedAt = deletedAt
            )
        }
    }
}