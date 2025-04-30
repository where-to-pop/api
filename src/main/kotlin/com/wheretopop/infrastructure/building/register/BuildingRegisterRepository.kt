package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterId

/**
 * BuildingRegister 애그리거트의 저장소 인터페이스
 * 이 인터페이스는 도메인 레이어에 정의되고, 인프라 레이어에서 구현됨
 */
interface BuildingRegisterRepository {
    suspend fun findById(id: BuildingRegisterId): BuildingRegister?
    suspend fun findByName(name: String): BuildingRegister?
    suspend fun findAll(): List<BuildingRegister>
    suspend fun save(building: BuildingRegister): BuildingRegister
    suspend fun save(buildings: List<BuildingRegister>): List<BuildingRegister>
    suspend fun deleteById(id: BuildingRegisterId)
}
