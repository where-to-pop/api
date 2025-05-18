package com.wheretopop.infrastructure.area.external.opendata.population

import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.domain.area.AreaInfo
import com.wheretopop.shared.infrastructure.entity.AreaPopulationEntity

/**
 * 지역 인구 데이터 리포지토리 인터페이스
 * JPA 기반으로 변경됨
 */
interface AreaPopulationRepository {
    fun save(entity: AreaPopulationEntity): AreaPopulationEntity
    fun save(entities: List<AreaPopulationEntity>): List<AreaPopulationEntity>
    fun findLatestByAreaId(areaId: AreaId): AreaPopulationEntity?
    fun findPopulationInsightByAreaId(areaId: AreaId): AreaInfo.PopulationInsight?
    fun findPopulationInsightsByAreaIds(areaIds: List<AreaId>): List<AreaInfo.PopulationInsight>
}