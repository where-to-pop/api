package com.wheretopop.infrastructure.building

import com.wheretopop.domain.building.Building
import com.wheretopop.domain.building.BuildingId
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Building 애그리거트의 저장소 인터페이스
 * JPA 기반으로 구현
 */
@Repository
@Transactional
interface BuildingRepository {
    fun findById(id: BuildingId): Building?
    fun findByName(name: String): Building?
    fun findByAddress(address: String): Building?
    fun findAll(): List<Building>
    fun save(building: Building): Building
    fun save(buildings: List<Building>): List<Building>
}
