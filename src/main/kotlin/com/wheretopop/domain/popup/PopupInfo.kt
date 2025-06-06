package com.wheretopop.domain.popup

import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.PopUpCategory
import java.util.*

class PopupInfo {

    /**
     * 팝업의 기본 정보를 담는 DTO입니다.
     * 목록 조회나 요약 정보 표시에 적합하며, 연결된 지역 및 건물 식별 정보를 포함합니다.
     */
    data class Basic(
        val id: PopupId,
        val name: String,
        val address: String,
        val latitude: Double?,
        val longitude: Double?,
        val description: String,
        val organizerName: String,
    ) {
        fun getContent(): String {
            return listOfNotNull(
                "팝업 제목: $name",
                "브랜드: $organizerName",
                "설명: $description",
            ).joinToString(separator = "\n")
        }
    }

    /**
     * 팝업의 상세 정보를 담는 DTO입니다.
     * 원본 팝업 데이터의 모든 필드와 함께, 연결된 지역/건물 정보를 포함
     */
    data class Detail(
        val id: PopupId,
        val name: String,
        val address: String,
        val latitude: Double?,
        val longitude: Double?,
        val description: String,
        val organizerName: String,

        val areaId: Long,
        val areaName: String,
        val buildingId: Long,

        val keywords: List<String>,
        val category: PopUpCategory,
        val targetAgeGroups: List<AgeGroup>,
        val brandKeywords: List<String>,
    ) {
        fun generateVectorId(): String {
            return UUID.nameUUIDFromBytes(id.toString().toByteArray()).toString()
        }

        fun getContentForEmbedding(): String {
            return listOfNotNull(
                "Title: $name",
                "Keywords: ${keywords.toString()}",
                "Target Age Group: ${targetAgeGroups.toString()}",
                "Area: $areaName",
                "Building: $address",
                "Brand: $organizerName",
                "BrandKeywords: ${brandKeywords.toString()}",
                description,
            ).joinToString(separator = "\n")
        }

        fun buildVectorMetadataMap(): Map<String, Any> {
            val metadata = mutableMapOf<String, Any>(
                "original_id" to id.toLong(),
                "popup_name" to name,
                "address" to address,
                "latitude" to (latitude ?: 0.0),
                "longitude" to (longitude ?: 0.0),
                "organizer_name" to organizerName,
                "description" to description,
                "area_id" to areaId,
                "area_name" to areaName,
                "building_id" to buildingId,
                "keywords" to keywords,
                "category" to category,
                "target_age_groups" to targetAgeGroups,
                "brand_keywords" to brandKeywords,
            )

            return metadata.toMap()
        }
    }

    data class WithScore(
        val popup: Detail,
        val score: Double,
    )

    data class Augmented(
        val keywords: List<String>,
        val category: PopUpCategory,
        val targetAgeGroups: List<AgeGroup>,
        val brandKeywords: List<String>,
    )
}