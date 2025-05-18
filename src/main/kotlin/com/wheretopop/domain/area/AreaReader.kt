package com.wheretopop.domain.area

import com.wheretopop.shared.domain.identifier.AreaId

/**
 * 권역 정보 조회를 위한 도메인 서비스 인터페이스
 */
interface AreaReader {
    fun findAll(): List<Area>
    fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area>
    fun findById(id: AreaId): Area?
    fun findByName(name: String): Area?
}