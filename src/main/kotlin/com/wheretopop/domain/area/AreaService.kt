package com.wheretopop.domain.area

interface AreaService {
    suspend fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main>
}