package com.wheretopop.domain.area

import java.time.LocalDate

/**
 * Area Aggregate Root
 */
class Area private constructor(
    val id: Long,
    val token: String,
    val name: String,
    val location: Location,
    val indicators: PopulationIndicators,
) {

    companion object {
        fun create(
            id: Long,
            token: String,
            name: String,
            location: Location,
            indicators: PopulationIndicators,
        ): Area {
            require(token.isNotBlank()) { "areaToken은 필수입니다." }
            require(name.isNotBlank()) { "지역 이름은 필수입니다." }
            return Area(id, token, name, location, indicators)
        }
    }

    fun isHighlyPopulated(threshold: Int): Boolean {
        return indicators.totalFloatingPopulation > threshold
    }

    fun markIndexed(now: LocalDate): Area {
        return Area(id, token, name, location, indicators)
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
