package com.wheretopop.interfaces.popup

import com.wheretopop.shared.enums.*
import io.swagger.v3.oas.annotations.media.Schema

object PopUpDto {

    @Schema(description = "PopUp Store DTO")
    data class PopUpResponse(    
        @Schema(description = "popup 대체키")
        val popupToken: String,

        @Schema(description = "팝업스토어 이름", example = "나이키 팝업스토어")
        val name: String,

        @Schema(description = "팝업스토어 설명", example = "나이키의 새로운 컬렉션을 소개하는 팝업스토어입니다.")
        val description: String,

        @Schema(description = "팝업스토어 카테고리")
        val category: PopUpCategory,

        @Schema(description = "팝업스토어 타입")
        val type: PopUpType,

        @Schema(description = "팝업스토어 브랜드 규모")
        val brandSize: BrandSize,

        @Schema(description = "팝업스토어 시작일")
        val startDate: String,

        @Schema(description = "팝업스토어 종료일")
        val endDate: String,

        @Schema(description = "팝업스토어 운영 시간")
        val operatingHours: String,

        @Schema(description = "팝업스토어 주소")
        val address: String,

        @Schema(description = "팝업스토어 이미지 URL")
        val imageUrl: String,

        @Schema(description = "팝업스토어 SNS 링크", nullable = true)
        val snsUrl: String? = null,
    )

    @Schema(description = "PopUp Store Search Request")
    data class SearchRequest(

        @Schema(description = "구 대체키")
        val areaToken: String ?= null,

        @Schema(description = "주요 성별")
        val dominantGender: Gender? = null,

        @Schema(description = "주요 연령대")
        val dominantAgeGroup: AgeGroup? = null,

        @Schema(description = "팝업스토어 브랜드 규모")
        val brandSize: BrandSize? = null,

        @Schema(description = "팝업스토어 카테고리")
        val category: PopUpCategory? = null,

        @Schema(description = "팝업스토어 타입")
        val type: PopUpType? = null,

        @Schema(description = "시/도 이름", example = "서울특별시")
        val provinceName: String? = null,

        @Schema(description = "구 이름", example = "종로구")
        val cityName: String? = null
    )
}
