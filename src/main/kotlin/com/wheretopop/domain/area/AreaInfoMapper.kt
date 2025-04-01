package com.wheretopop.domain.area

class AreaInfoMapper {

    fun of(domain: Area): AreaInfo.Main {
        return AreaInfo.Main(
            areaId = domain.id,
            areaToken = domain.areaToken,
            provinceName = domain.location.province,
            cityName = domain.location.city,
            totalFloatingPopulation = domain.indicators.totalFloatingPopulation,
            maleFloatingPopulation = domain.indicators.maleFloatingPopulation,
            femaleFloatingPopulation = domain.indicators.femaleFloatingPopulation,
            populationDensity = domain.indicators.populationDensity,
        )
    }

    fun of(domains: List<Area>): List<AreaInfo.Main> {
        return domains.map(::of)
    }
}