package com.wheretopop.domain.popup


/**
 * PopupInfo DTO 객체 생성을 담당하는 매퍼 클래스입니다.
 * 원본 SourcePopupData 객체와 외부에서 얻은 지역(Area)/건물(Building) 정보를 조합하여
 * 다양한 PopupInfo DTO를 생성합니다.
 */

class PopupInfoMapper {
    companion object {
        fun toBasic(
            id: PopupId,
            name: String,
            address: String,
            latitude: Double?,
            longitude: Double?,
            description: String,
            organizerName: String
        ): PopupInfo.Basic {
            return PopupInfo.Basic(
                id = id,
                name = name,
                address = address,
                latitude = latitude,
                longitude = longitude,
                description = description,
                organizerName = organizerName,
            )
        }

        /**
         * 지역/건물 정보를 바탕으로 PopupInfo.Detail DTO를 생성합니다.
         * 메타데이터는 지역/건물 정보로 보강될 수 있습니다.
         */
        fun toDetail(
            basicPopupInfo: PopupInfo.Basic,
            areaId: Long,
            areaName: String,
            buildingId: Long
        ): PopupInfo.Detail {
            return PopupInfo.Detail(
                id = basicPopupInfo.id,
                name = basicPopupInfo.name,
                address = basicPopupInfo.address,
                latitude = basicPopupInfo.latitude,
                longitude = basicPopupInfo.longitude,
                description = basicPopupInfo.description,
                organizerName = basicPopupInfo.organizerName,
                areaId = areaId,
                areaName = areaName,
                buildingId = buildingId,
            )
        }
        fun toDetail(
            id: PopupId,
            name: String,
            address: String,
            latitude: Double?,
            longitude: Double?,
            description: String,
            organizerName: String,
            areaId: Long,
            areaName: String,
            buildingId: Long
        ): PopupInfo.Detail {
            return PopupInfo.Detail(
                id = id,
                name = name,
                address = address,
                latitude = latitude,
                longitude = longitude,
                description = description,
                organizerName = organizerName,
                areaId = areaId,
                areaName = areaName,
                buildingId = buildingId
            )
        }

        /**
         * 점수(score)와 함께 제공될 PopupInfo.WithScore DTO를 생성합니다.
         */
        fun toWithScore(
            sourcePopupInfo: PopupInfo.Detail,
            score: Double,
        ): PopupInfo.WithScore {
            return PopupInfo.WithScore(
                popup = sourcePopupInfo,
                score = score
            )
        }

        private fun buildAugmentedVectorMetadata(sourcePopupInfo: PopupInfo.Detail): Map<String, Any> {
            return sourcePopupInfo.buildVectorMetadataMap().toMutableMap()
        }
    }
}