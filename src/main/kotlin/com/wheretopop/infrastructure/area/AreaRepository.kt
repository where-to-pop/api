package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.shared.model.UniqueId
import java.util.Optional

/**
 * Area 애그리거트의 저장소 인터페이스
 * 이 인터페이스는 도메인 레이어에 정의되고, 인프라 레이어에서 구현됨
 */
interface AreaRepository {
    /**
     * ID로 Area 조회 (SQL)
     */
    fun findById(id: UniqueId): Optional<Area>

    /**
     * 이름으로 Area 조회 (SQL)
     */
    fun findByName(name: String): Optional<Area>

    /**
     * 검색 조건을 통한 Area 조회 (SQL)
     */
    fun findByCondition(criteria: AreaCriteria): List<Area>
    
    /**
     * 지역 ID로 Area 목록 조회 (SQL)
     */
    fun findByRegionId(regionId: Long): List<Area>
    
    /**
     * 위치 기반 Area 조회 (SQL)
     */
    fun findByLocation(latitude: Double, longitude: Double, radiusKm: Double): List<Area>
    
    /**
     * 비즈니스 타입으로 Area 조회 (SQL)
     */
    fun findByBusinessType(businessType: String): List<Area>

    /**
     * Area 저장 (신규 생성 또는 업데이트) (SQL)
     */
    fun save(area: Area): Area

    /**
     * ID로 Area 삭제 (SQL)
     */
    fun deleteById(id: UniqueId)
    
    /**
     * 검색 조건을 통한 Area 검색 (ES)
     */
    fun search(criteria: AreaCriteria): List<Area>
    
    /**
     * 키워드 기반 Area 검색 (ES)
     */
    fun searchByKeyword(keyword: String): List<Area>
    
    /**
     * 인구통계 기반 Area 검색 (ES)
     */
    fun searchByDemographic(criteria: AreaCriteria.DemographicCriteria): List<Area>
    
    /**
     * 상업 정보 기반 Area 검색 (ES)
     */
    fun searchByCommercial(criteria: AreaCriteria.CommercialCriteria): List<Area>
}
