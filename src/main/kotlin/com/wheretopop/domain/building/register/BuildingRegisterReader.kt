package com.wheretopop.domain.building.register

/**
 * 권역 정보 조회를 위한 도메인 서비스 인터페이스
 */
interface BuildingRegisterReader {
    fun findAll(): List<BuildingRegister>
    fun findBuildingRegisters(criteria: BuildingRegisterCriteria.SearchBuildingRegisterCriteria): List<BuildingRegister>
    fun findById(id: BuildingRegisterId): BuildingRegister?
}