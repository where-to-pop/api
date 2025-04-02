package com.wheretopop.interfaces.building

import com.wheretopop.shared.enums.BuildingSize
import com.wheretopop.shared.enums.PopUpFrequency
import io.swagger.v3.oas.annotations.media.Schema

object BuildingDto {

    @Schema(description = "Building DTO")
    data class BuildingResponse(
        @Schema(description = "빌딩 대체키")
        val buildingToken: String,

        @Schema(description = "빌딩 이름", example = "종로타워")
        val name: String,

        @Schema(description = "빌딩 설명", example = "종로구의 대표적인 상업 빌딩")
        val description: String,

        @Schema(description = "빌딩 주소", example = "서울특별시 종로구 종로1가 123-45")
        val address: String,

        @Schema(description = "빌딩 층수", example = "15")
        val totalFloors: Int,

        @Schema(description = "빌딩 연면적(제곱미터)", example = "5000")
        val totalArea: Int,

        @Schema(description = "빌딩 완공년도", example = "2010")
        val completionYear: Int,

        @Schema(description = "빌딩 이미지 URL", example = "https://example.com/building-image.jpg")
        val imageUrl: String,

        @Schema(description = "빌딩 관리사 연락처", example = "02-1234-5678")
        val contactNumber: String,

        @Schema(description = "빌딩 관리사 이메일", example = "manager@building.com")
        val contactEmail: String,

        @Schema(description = "빌딩 운영 시간", example = "09:00-18:00")
        val operatingHours: String,

        @Schema(description = "주차 가능 여부", example = "true")
        val hasParking: Boolean,

        @Schema(description = "주차 가능 대수", example = "50")
        val parkingCapacity: Int?,

        @Schema(description = "엘리베이터 보유 여부", example = "true")
        val hasElevator: Boolean,

        @Schema(description = "엘리베이터 대수", example = "4")
        val elevatorCount: Int?,

        @Schema(description = "화장실 보유 여부", example = "true")
        val hasRestroom: Boolean,

        @Schema(description = "보안시설 보유 여부", example = "true")
        val hasSecurity: Boolean,

        @Schema(description = "CCTV 보유 여부", example = "true")
        val hasCctv: Boolean,

        @Schema(description = "CCTV 대수", example = "20")
        val cctvCount: Int?,

        @Schema(description = "화재경보기 보유 여부", example = "true")
        val hasFireAlarm: Boolean,

        @Schema(description = "소화기 보유 여부", example = "true")
        val hasFireExtinguisher: Boolean,

        @Schema(description = "비상구 보유 여부", example = "true")
        val hasEmergencyExit: Boolean,

        @Schema(description = "비상구 개수", example = "4")
        val emergencyExitCount: Int?
    )


    @Schema(description = "Building Search Request")
    data class SearchRequest(
        @Schema(description = "구 대체키")
        val areaToken: String? = null,

        @Schema(description = "빌딩 규모(연면적)")
        val buildingSize: BuildingSize? = null,

        @Schema(description = "팝업스토어 운영 빈도")
        val popUpFrequency: PopUpFrequency? = null,

        @Schema(description = "최소 층수", example = "10")
        val minFloors: Int? = null,

        @Schema(description = "최대 층수", example = "20")
        val maxFloors: Int? = null,

        @Schema(description = "주차 가능 여부")
        val hasParking: Boolean? = null,

        @Schema(description = "엘리베이터 보유 여부")
        val hasElevator: Boolean? = null,

        @Schema(description = "화장실 보유 여부")
        val hasRestroom: Boolean? = null,

        @Schema(description = "보안시설 보유 여부")
        val hasSecurity: Boolean? = null
    )
} 