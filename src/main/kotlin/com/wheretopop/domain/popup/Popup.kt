package com.wheretopop.domain.popup

import java.time.Instant

class Popup private constructor(
    val id: PopupId,
    val name: String,
    val address: String,
    val createdAt: Instant,
    var deletedAt: Instant? = null
) {
    companion object {
        fun create(
            id: PopupId = PopupId.create(),
            name: String,
            address: String,
            createdAt: Instant = Instant.now(),
            deletedAt: Instant? = null
        ): Popup {
            require(name.isNotBlank()) {"팝업 이름은 필수입니다."}
            return Popup(
                id = id,
                name = name,
                address = address,
                createdAt = createdAt,
                deletedAt = deletedAt
            )
        }
    }
}