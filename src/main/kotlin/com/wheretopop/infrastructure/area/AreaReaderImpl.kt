package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaReader
import com.wheretopop.infrastructure.area.bootstrap.AreaSeedData
import com.wheretopop.shared.domain.identifier.AreaId
import org.springframework.stereotype.Component
import com.wheretopop.shared.util.CalculateUtil

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

    override fun findByCoordinates(latitude: Double, longitude: Double): Area? {
        val seedAreas = AreaSeedData.createDefaultAreas()
        if (seedAreas.isEmpty()) {
            return null
        }
        var closestArea: Area? = null
        var minDistance = Double.MAX_VALUE

        for (area in seedAreas) {
            val distance = CalculateUtil.calculateDistance(
                lat1 = latitude,
                lon1 = longitude,
                lat2 = area.location.latitude,
                lon2 = area.location.longitude,
            )

            if (distance < minDistance) {
                minDistance = distance
                closestArea = area
            }
        }
        return closestArea
    }

    override fun findNearestArea(latitude: Double, longitude: Double): Area? {
        return areaRepository.findNearest(latitude, longitude)
    }
}