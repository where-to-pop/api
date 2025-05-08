package com.wheretopop.domain.building

/**
 * 권역 정보 조회를 위한 도메인 서비스 인터페이스
 */
interface BuildingReader {
    suspend fun findAll(): List<Building>
    suspend fun findBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<Building>
    suspend fun findById(id: BuildingId): Building?
    suspend fun findByName(name: String): Building?
    suspend fun findByAddress(address: String): Building?
}