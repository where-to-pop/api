package com.wheretopop.domain.area

class AreaInfoMapper {

    fun of(domain: Area): AreaInfo.Main {
        return AreaInfo.Main(
            id = domain.id,
            token = domain.token,
            name = domain.name,
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