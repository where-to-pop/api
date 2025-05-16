package com.wheretopop.domain.area

interface AreaService {
    suspend fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main>
    suspend fun findAll(): List<AreaInfo.Main>
    suspend fun getAreaDetailById(id: AreaId): AreaInfo.Detail
}