package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaStore
import org.springframework.stereotype.Component

/**
 * AreaStore 인터페이스 구현체
 * 도메인 레이어와 인프라 레이어를 연결하는 역할을 담당
 */
@Component
class AreaStoreImpl(
    private val areaRepository: AreaRepository
) : AreaStore {

    override suspend fun save(area: Area): Area {
        return areaRepository.save(area)
    }
    override suspend fun save(areas: List<Area>): List<Area> {
        return areaRepository.save(areas)
    }
    override suspend fun delete(area: Area) {
        areaRepository.deleteById(area.id)
    }   
    override suspend fun delete(areas: List<Area>) {
        areas.forEach { area ->
            areaRepository.deleteById(area.id)
        }
    }
} 