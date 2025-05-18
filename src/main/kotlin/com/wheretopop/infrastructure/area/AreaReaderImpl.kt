package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaReader
import com.wheretopop.shared.domain.identifier.AreaId
import org.springframework.stereotype.Component

/**
 * AreaReader 인터페이스의 구현체
 * Repository 패턴을 통해 조회를 위임합니다.
 */
@Component
class AreaReaderImpl(
    private val areaRepository: AreaRepository
) : AreaReader {

    override fun findById(id: AreaId): Area? {
        return areaRepository.findById(id)
    }
    
    override fun findByName(name: String): Area? {
        return areaRepository.findByName(name)
    }

    override fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area> {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Area> {
        return areaRepository.findAll()
    }

    override fun findNearestArea(latitude: Double, longitude: Double): Area? {
        return areaRepository.findNearest(latitude, longitude)
    }
}