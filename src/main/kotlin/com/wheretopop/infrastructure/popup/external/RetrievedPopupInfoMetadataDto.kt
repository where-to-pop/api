package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoMapper

data class RetrievedPopupInfoMetadata(
    val originalId: Long?,
    val popupName: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val organizerName: String?,
    val description: String?,
    val areaId: Long?,
    val areaName: String?,
    val buildingId: Long?,
) {
    fun toDomain(): PopupInfo.Detail? {
        if (originalId == null || popupName == null || address == null || description == null || organizerName == null || areaId == null || areaName == null || buildingId == null) {
            return null
        }

        return PopupInfoMapper.toDetail(
            id = PopupId.of(originalId),
            name = popupName,
            address = address,
            latitude = latitude,
            longitude = longitude,
            description = description,
            organizerName = organizerName,
            areaId = areaId,
            areaName = areaName,
            buildingId = buildingId,
        )
    }

    companion object {
        fun fromMap(metadataMap: Map<String, Any?>): RetrievedPopupInfoMetadata {
            return RetrievedPopupInfoMetadata(
                originalId = metadataMap["original_id"] as? Long,
                popupName = metadataMap["popup_name"] as? String,
                address = metadataMap["address"] as? String,
                latitude = metadataMap["latitude"] as? Double,
                longitude = metadataMap["longitude"] as? Double,
                description = metadataMap["description"] as? String,
                organizerName = metadataMap["organizer_name"] as? String,
                areaId = metadataMap["area_id"] as? Long,
                areaName = metadataMap["area_name"] as? String,
                buildingId = metadataMap["building_id"] as? Long,
            )
        }


    }
}
