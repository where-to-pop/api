package com.wheretopop.domain.area

/* 도메인 계층 외부에 넘겨 줄 Area 정보 */
class AreaInfo {

    data class Main (
        val areaId: Long,
        val areaToken: String,

        val provinceName: String,
        val cityName: String,

        val totalFloatingPopulation: Int,
        val maleFloatingPopulation: Int,
        val femaleFloatingPopulation: Int,

        val populationDensity: Int,
    )
}