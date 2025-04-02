package com.wheretopop.interfaces.area

import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.FloatingPopulation
import com.wheretopop.shared.enums.Gender
import com.wheretopop.shared.enums.PopulationDensity
import io.swagger.v3.oas.annotations.media.Schema

object AreaDto {

    @Schema(description = "Area DTO")
    data class AreaResponse(
        @Schema(description = "area 대체키")
        val areaToken: String,

        @Schema(description = "동의 이름", example = "종로1가")
        val name: String,

        @Schema(description = "동이 속한 시/도의 이름", example = "서울광역시")
        val provinceName: String,
        @Schema(description = "동이 속한 구의 이름", example = "종로구")
        val cityName: String,

        @Schema(description = "전체 유동인구 수 ")
        val totalFloatingPopulation: Int,
        @Schema(description = "남성 유동인구 수")
        val maleFloatingPopulation: Int,
        @Schema(description = "여성 유동인구 수")
        val femaleFloatingPopulation: Int,

        @Schema(description = "인구 밀집도")
        val populationDensity: Int,
    )


    @Schema(description = "Area Search Request")
    data class SearchRequest(
        @Schema(description = "유동인구")
        val floatingPopulation: FloatingPopulation? = null,

        @Schema(description = "인구 밀집도")
        val populationDensity: PopulationDensity? = null,

        @Schema(description = "주요 성별")
        val dominantGender: Gender? = null,

        @Schema(description = "주요 연령대")
        val dominantAgeGroup: AgeGroup? = null,
    )
}
