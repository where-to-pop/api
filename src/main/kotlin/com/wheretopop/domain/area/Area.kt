package com.wheretopop.domain.area

import com.wheretopop.shared.model.Location
import java.time.Instant

/**
 * Area Aggregate Root
 * 특정 권역을 나타내는 애그리거트 루트 클래스
 */
class Area private constructor(
    val id: AreaId,
    val name: String,
    val description: String,
    var location: Location,
    val createdAt: Instant,
    var updatedAt: Instant,
    var deletedAt: Instant? = null
) {

    companion object {
        fun create(
            id: AreaId = AreaId.create(),
            name: String,
            description: String,
            location: Location,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant?

        ): Area {
            require(name.isNotBlank()) { "지역 이름은 필수입니다." }
            return Area(
                id = id,
                name = name,
                description = description,
                location = location,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }

    /**
     * 권역의 위치 정보 업데이트
     * @param newLocation 새 위치 정보
     * @return 업데이트된 Area 객체
     */
    fun updateLocation(newLocation: Location): Area {
        this.location = newLocation
        this.updatedAt = Instant.now()
        return this
    }

}
