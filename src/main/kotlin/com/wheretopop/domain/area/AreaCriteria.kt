package com.wheretopop.domain.area

import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.Gender

object AreaCriteria {
    data class SearchAreaCriteria(
        val minFloatingPopulation: Int? = null,
        val maxFloatingPopulation: Int? = null,
        val minPopulationDensity: Int? = null,
        val maxPopulationDensity: Int? = null,
        val dominantGender: Gender? = null,
        val dominantAgeGroup: AgeGroup? = null,
    )
}