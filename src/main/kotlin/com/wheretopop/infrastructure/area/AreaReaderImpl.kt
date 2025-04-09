package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaReader
import com.wheretopop.shared.model.UniqueId
import org.springframework.stereotype.Component
import java.util.Optional

/**
 * AreaReader 인터페이스의 구현체
 * Repository 패턴을 통해 조회를 위임합니다.
 */
@Component
class AreaReaderImpl(
    private val areaRepository: AreaRepository
) : AreaReader {
    /**
     * 검색 조건을 통한 Area 조회
     * 키워드 검색이나 복잡한 조건은 Elasticsearch 사용
     */
    override fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area> {
        // 검색 성능이 중요한 경우 Elasticsearch 사용
        return areaRepository.search(criteria)
    }
    
    /**
     * ID로 Area 조회
     * 기본 CRUD는 SQL 사용
     */
    override fun findById(id: UniqueId): Optional<Area> {
        // ID 조회는 SQL 사용
        return areaRepository.findById(id)
    }
    
    /**
     * 이름으로 Area 조회
     * 기본 CRUD는 SQL 사용
     */
    override fun findByName(name: String): Optional<Area> {
        // 정확한 이름 조회는 SQL 사용
        return areaRepository.findByName(name)
    }
    
    /**
     * 인구통계 기반 검색 조건으로 Area 조회
     * 복잡한 검색은 Elasticsearch 사용
     */
    override fun findByDemographicCriteria(criteria: AreaCriteria.DemographicCriteria): List<Area> {
        // 복잡한 검색은 Elasticsearch 사용
        return areaRepository.searchByDemographic(criteria)
    }
    
    /**
     * 상업 정보 기반 검색 조건으로 Area 조회
     * 복잡한 검색은 Elasticsearch 사용
     */
    override fun findByCommercialCriteria(criteria: AreaCriteria.CommercialCriteria): List<Area> {
        // 복잡한 검색은 Elasticsearch 사용
        return areaRepository.searchByCommercial(criteria)
    }
}