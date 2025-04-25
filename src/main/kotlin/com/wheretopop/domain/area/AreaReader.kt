package com.wheretopop.domain.area

/**
 * 권역 정보 조회를 위한 도메인 서비스 인터페이스
 */
interface AreaReader {
    suspend fun findAll(): List<Area>
    suspend fun findAreas(criteria: AreaCriteria.SearchAreaCriteria): List<Area>
    suspend fun findById(id: AreaId): Area?
    suspend fun findByName(name: String): Area?
}