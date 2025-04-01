package com.wheretopop.interfaces.area

import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.interfaces.area.AreaDto.AreaResponse
import com.wheretopop.interfaces.area.AreaDto.ListResponse

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
            areaToken = info.areaToken,
            provinceName = info.provinceName,
            cityName = info.cityName,
            totalFloatingPopulation = info.totalFloatingPopulation,
            maleFloatingPopulation = info.maleFloatingPopulation,
            femaleFloatingPopulation = info.femaleFloatingPopulation,
            populationDensity = info.populationDensity
        )
    }

    fun toAreaResponses(infoList: List<AreaInfo.Main>): ListResponse {
        val results = infoList.map { toAreaResponse(it) }
        return ListResponse(results = results, totalCount = results.size)
    }
}
