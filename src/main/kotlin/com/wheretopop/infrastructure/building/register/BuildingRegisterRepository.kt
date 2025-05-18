package com.wheretopop.infrastructure.building.register

import com.wheretopop.domain.building.register.BuildingRegister
import com.wheretopop.domain.building.register.BuildingRegisterId
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * BuildingRegister 애그리거트의 저장소 인터페이스
 * JPA 기반으로 구현
 */
@Repository
@Transactional
interface BuildingRegisterRepository {
    fun findById(id: BuildingRegisterId): BuildingRegister?
    fun findByName(name: String): BuildingRegister?
    fun findAll(): List<BuildingRegister>
    fun save(building: BuildingRegister): BuildingRegister
    fun save(buildings: List<BuildingRegister>): List<BuildingRegister>
}
