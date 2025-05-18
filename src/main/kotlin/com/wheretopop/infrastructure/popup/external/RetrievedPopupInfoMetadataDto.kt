package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo

data class RetrievedPopupInfoMetadata(
    val originalId: Long?,
    val popupName: String?,
    val address: String?,
    val organizerName: String?,
    val description: String?
) {
    fun toDomain(): PopupInfo? {
        if (originalId == null || popupName == null || address == null || description == null || organizerName == null) {
            return null
        }

        return PopupInfo(
            id = PopupId.of(originalId),
            name = popupName,
            address = address,
            description = description,
            organizerName = organizerName,
        )
    }

    companion object {
        fun fromMap(metadataMap: Map<String, Any?>): RetrievedPopupInfoMetadata {
            return RetrievedPopupInfoMetadata(
                originalId = metadataMap["original_id"] as? Long,
                popupName = metadataMap["popup_name"] as? String,
                address = metadataMap["address"] as? String,
                organizerName = metadataMap["organizer_name"] as? String,
                description = metadataMap["description"] as? String,
            )
        }


    }
}
