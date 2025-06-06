package com.wheretopop.infrastructure.popup.external

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.domain.popup.PopupInfoMapper
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.PopUpCategory

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
    val keywords: List<String>?,
    val category: PopUpCategory?,
    val targetAgeGroups: List<AgeGroup>?,
    val brandKeywords: List<String>?,
) {

    private fun hasNullRequiredFields(): Boolean {
        return listOf(
            originalId, popupName, address, description, organizerName,
            areaId, areaName, buildingId,
            keywords, category, targetAgeGroups, brandKeywords
        ).any { it == null }
    }

    fun toDomain(): PopupInfo.Detail? {
        if (hasNullRequiredFields()) return null

        return PopupInfo.Detail(
            id = PopupId.of(originalId!!),
            name = popupName!!,
            address = address!!,
            latitude = latitude,
            longitude = longitude,
            description = description!!,
            organizerName = organizerName!!,
            areaId = areaId!!,
            areaName = areaName!!,
            buildingId = buildingId!!,
            keywords = keywords!!,
            category = category!!,
            targetAgeGroups = targetAgeGroups!!,
            brandKeywords = brandKeywords!!,
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
                keywords = metadataMap["keywords"] as? List<String>,
                category = metadataMap["category"] as? PopUpCategory,
                targetAgeGroups = metadataMap["target_age_groups"] as? List<AgeGroup>,
                brandKeywords = metadataMap["brand_keywords"] as? List<String>,
            )
        }


    }
}
