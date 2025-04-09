package com.wheretopop.domain.area

import com.wheretopop.shared.model.UniqueId
import java.util.Optional

/**
 * 권역 정보 조회를 위한 도메인 서비스 인터페이스
 */
interface AreaReader {
    /**
     * 검색 조건을 통한 Area 조회 (검색 최적화)
     */
    fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area>
    
    /**
     * ID로 Area 조회
     */
    fun findById(id: UniqueId): Optional<Area>
    
    /**
     * 이름으로 Area 조회
     */
    fun findByName(name: String): Optional<Area>
    
    /**
     * 인구통계 기반 검색 조건으로 Area 조회
     */
    fun findByDemographicCriteria(criteria: AreaCriteria.DemographicCriteria): List<Area>
    
    /**
     * 상업 정보 기반 검색 조건으로 Area 조회
     */
    fun findByCommercialCriteria(criteria: AreaCriteria.CommercialCriteria): List<Area>
}