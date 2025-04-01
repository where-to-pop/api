package com.wheretopop.domain.area

interface AreaReader {
    fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area>
}