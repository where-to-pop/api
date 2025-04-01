package com.wheretopop.gateway.interfaces.area

import com.wheretopop.gateway.shared.enums.AgeGroup
import com.wheretopop.gateway.shared.enums.FloatingPopulation
import com.wheretopop.gateway.shared.enums.Gender
import com.wheretopop.gateway.shared.enums.PopulationDensity
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

object AreaDto {

    data class AreaResponse(
        val areaToken: String,
        val areaCode: String,
        val regionName: String,
        val floatingPopulation: Int,
        val populationDensity: Int,
        val dominantGender: Gender,
        val dominantAgeGroup: AgeGroup,
        val score: BigDecimal // 정렬 기준 점수 (ex. 추천 우선순위)
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

        @Schema(description = "정렬/우선순위 계산용 필터")
        val minScore: BigDecimal? = null // 정렬/우선순위 계산용 점수 필터
    )

    data class ListResponse(
        val results: List<AreaResponse>,
        val totalCount: Int
    )
}
