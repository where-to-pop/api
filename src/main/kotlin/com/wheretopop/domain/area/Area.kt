package com.wheretopop.domain.area

import com.wheretopop.shared.model.Location
import com.wheretopop.shared.model.UniqueId
import java.time.LocalDateTime

/**
 * Area Aggregate Root
 * 특정 권역을 나타내는 애그리거트 루트 클래스
 */
class Area private constructor(
    val id: UniqueId,
    val name: String,
    val description: String,
    var location: Location,
    val regionId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var deletedAt: LocalDateTime? = null
) {
    // 통계 정보에 대한 읽기 전용 접근자

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            name: String,
            description: String,
            location: Location,
            regionId: Long? = null,
        ): Area {
            require(name.isNotBlank()) { "지역 이름은 필수입니다." }
            return Area(
                id = id,
                name = name,
                description = description,
                location = location,
                regionId = regionId,
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
        this.updatedAt = LocalDateTime.now()
        return this
    }

}
