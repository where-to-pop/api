package com.wheretopop.interfaces.area

import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.interfaces.area.AreaDto.AreaResponse

class AreaDtoMapper {

    fun toCriteria(request: AreaDto.SearchRequest): AreaCriteria.SearchAreaCriteria = AreaCriteria.SearchAreaCriteria(
        minFloatingPopulation = request.floatingPopulation?.min,
        maxFloatingPopulation = request.floatingPopulation?.max,
        minPopulationDensity = request.populationDensity?.min,
        maxPopulationDensity = request.populationDensity?.max,
        dominantGender = request.dominantGender,
        dominantAgeGroup = request.dominantAgeGroup,
    )


    fun toAreaResponse(info: AreaInfo.Main): AreaResponse {
        return AreaResponse(
            areaToken = info.token,
            name = info.name,
            provinceName = info.provinceName,
            cityName = info.cityName,
            totalFloatingPopulation = info.totalFloatingPopulation,
            maleFloatingPopulation = info.maleFloatingPopulation,
            femaleFloatingPopulation = info.femaleFloatingPopulation,
            populationDensity = info.populationDensity
        )
    }

    fun toAreaResponses(infoList: List<AreaInfo.Main>): List<AreaResponse> {
        val results = infoList.map { toAreaResponse(it) }
        return results
    }
}
