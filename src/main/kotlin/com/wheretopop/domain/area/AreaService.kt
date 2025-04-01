package com.wheretopop.domain.area

interface AreaService {
    fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main>
}