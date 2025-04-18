package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.model.Location

/**
 * 인프라 계층 엔티티와 도메인 모델 간 변환을 담당하는 매퍼
 */
class AreaMapper {

    /**
     * AreaEntity Area 도메인 모델로 변환
     */
    fun toDomain(entity: AreaEntity): Area {
        val location = entity.latitude?.let { lat ->
            entity.longitude?.let { lng ->
                Location.of(lat, lng)
            }
        } ?: Location.of(0.0, 0.0)

        val area = Area.create(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            location = location,
            regionId = entity.regionId
        )

        return area
    }

    /**
     * Area 도메인 모델을 AreaEntity 변환
     */
    fun toEntity(domain: Area): AreaEntity {
        val entity = AreaEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            latitude = domain.location.latitude,
            longitude = domain.location.longitude,
            regionId = domain.regionId
        )

        return entity
    }
} 