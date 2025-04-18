package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.domain.area.AreaReader
import com.wheretopop.shared.model.UniqueId
import org.springframework.stereotype.Component
import java.util.*

/**
 * AreaReader 인터페이스의 구현체
 * Repository 패턴을 통해 조회를 위임합니다.
 */
@Component
class AreaReaderImpl(
    private val areaRepository: AreaRepository
) : AreaReader {

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

    override fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area> {
        TODO("Not yet implemented")
    }

}