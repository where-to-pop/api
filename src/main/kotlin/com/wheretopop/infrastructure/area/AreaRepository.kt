package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaId

/**
 * Area 애그리거트의 저장소 인터페이스
 * 이 인터페이스는 도메인 레이어에 정의되고, 인프라 레이어에서 구현됨
 */
interface AreaRepository {
    suspend fun findById(id: AreaId): Area?
    suspend fun findByName(name: String): Area?
    suspend fun findAll(): List<Area>
    suspend fun save(area: Area): Area
    suspend fun save(areas: List<Area>): List<Area>
    suspend fun deleteById(id: AreaId)
}
