package com.wheretopop.infrastructure.building

import com.wheretopop.domain.building.Building
import com.wheretopop.domain.building.BuildingId

/**
 * Building 애그리거트의 저장소 인터페이스
 * 이 인터페이스는 도메인 레이어에 정의되고, 인프라 레이어에서 구현됨
 */
interface BuildingRepository {
    suspend fun findById(id: BuildingId): Building?
    suspend fun findByName(name: String): Building?
    suspend fun findAll(): List<Building>
    suspend fun save(building: Building): Building
    suspend fun save(buildings: List<Building>): List<Building>
    suspend fun deleteById(id: BuildingId)
}
