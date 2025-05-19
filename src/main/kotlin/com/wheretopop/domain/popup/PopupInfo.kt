package com.wheretopop.domain.popup

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
    )

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
    ) {
        fun generateVectorId(): String {
            return UUID.nameUUIDFromBytes(id.toString().toByteArray()).toString()
        }

        fun getContentForEmbedding(): String {
            return listOfNotNull(
                name,
                "Area: $areaName",
                address,
                organizerName,
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
            )

            return metadata.toMap()
        }
    }

    data class WithScore(
        val popup: Detail,
        val score: Double,
    )
}