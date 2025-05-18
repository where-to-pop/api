package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.domain.identifier.AreaId

/**
 * 지역(Area) 리포지토리 인터페이스
 * JPA 기반으로 변경됨
 */
interface AreaRepository {
    fun findById(id: AreaId): Area?
    fun findByName(name: String): Area?
    fun findAll(): List<Area>
    fun save(area: Area): Area
    fun save(areas: List<Area>): List<Area>
    fun deleteById(id: AreaId)
    fun findNearest(latitude: Double, longitude: Double, maxDistanceKm: Double = 10.0): Area?
}
