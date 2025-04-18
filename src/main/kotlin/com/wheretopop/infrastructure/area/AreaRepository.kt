package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.model.UniqueId
import java.util.*

/**
 * Area 애그리거트의 저장소 인터페이스
 * 이 인터페이스는 도메인 레이어에 정의되고, 인프라 레이어에서 구현됨
 */
interface AreaRepository {
    fun findById(id: UniqueId): Optional<Area>

    /**
     * 이름으로 Area 조회 (SQL)
     */
    fun findByName(name: String): Optional<Area>


    /**
     * 지역 ID로 Area 목록 조회 (SQL)
     */
    fun findByRegionId(regionId: Long): List<Area>

    /**
     * 위치 기반 Area 조회 (SQL)
     */
    fun findByLocation(latitude: Double, longitude: Double, radiusKm: Double): List<Area>

    /**
     * Area 저장 (신규 생성 또는 업데이트) (SQL)
     */
    fun save(area: Area): Area

    /**
     * ID로 Area 삭제 (SQL)
     */
    fun deleteById(id: UniqueId)
    
}
