package com.wheretopop.domain.building

/**
 * 권역 정보 조회를 위한 도메인 서비스 인터페이스
 */
interface BuildingReader {
    fun findAll(): List<Building>
    fun findBuildings(criteria: BuildingCriteria.SearchBuildingCriteria): List<Building>
    fun findById(id: BuildingId): Building?
    fun findByAddress(address: String): Building?
}