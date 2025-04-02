package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaReader
import org.springframework.stereotype.Component

@Component
class AreaReaderImpl(
//    private val elasticsearchAreaRepository: ElasticsearchAreaRepository
): AreaReader {
    override fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area> {
        return AreaMockData.areaList
    }
}