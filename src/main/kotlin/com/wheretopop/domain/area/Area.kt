package com.wheretopop.domain.area

import java.time.LocalDate

/**
 * Area Aggregate Root
 */
class Area private constructor(
    val id: Long,
    val areaToken: String,
    val name: String,
    val location: Location,
    val indicators: PopulationIndicators,
) {

    companion object {
        fun create(
            id: Long,
            areaToken: String,
            name: String,
            location: Location,
            indicators: PopulationIndicators,
        ): Area {
            require(areaToken.isNotBlank()) { "areaToken은 필수입니다." }
            require(name.isNotBlank()) { "지역 이름은 필수입니다." }
            return Area(id, areaToken, name, location, indicators)
        }
    }

    fun isHighlyPopulated(threshold: Int): Boolean {
        return indicators.totalFloatingPopulation > threshold
    }

    fun markIndexed(now: LocalDate): Area {
        return Area(id, areaToken, name, location, indicators)
    }
}


data class Location(
    val province: String,
    val city: String,
)


data class PopulationIndicators(
    val totalFloatingPopulation: Int,
    val maleFloatingPopulation: Int,
    val femaleFloatingPopulation: Int,
    val populationDensity: Int
)
