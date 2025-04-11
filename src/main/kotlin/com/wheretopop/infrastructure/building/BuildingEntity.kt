package com.wheretopop.infrastructure.building

import com.wheretopop.shared.enums.BuildingSize
import com.wheretopop.shared.enums.FloatingPopulation
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import com.wheretopop.shared.model.Location

@Entity
@Table(name = "buildings", indexes = [Index(name = "idx_building_area_id", columnList = "area_id"), Index(name = "idx_building_region_id", columnList = "region_id")])
@Comment("건물 정보 테이블")
class BuildingEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("건물 고유 식별자 (Snowflake ID)")
    @JdbcTypeCode(SqlTypes.BIGINT)
    var id: UniqueId = UniqueId.create(),

    @Column(name = "name", nullable = false)
    @Comment("건물 이름")
    var name: String,

    @Column(name = "address")
    @Comment("건물 주소")
    var address: String? = null,

    @Column(name = "region_id")
    @Comment("지역 ID (FK - regions 테이블)")
    var regionId: Long? = null,

    @Column(name = "area_id")
    @Comment("권역 ID (FK - areas 테이블)")
    var areaId: Long? = null,

    @Column(name = "latitude")
    @Comment("위도")
    var latitude: Double? = null,

    @Column(name = "longitude")
    @Comment("경도")
    var longitude: Double? = null,

    @Column(name = "total_floor_area")
    @Comment("총 바닥 면적 (m²)")
    var totalFloorArea: Double? = null,

    @Column(name = "has_elevator")
    @Comment("엘리베이터 유무")
    var hasElevator: Boolean? = null,

    @Column(name = "parking_info")
    @Comment("주차 정보")
    var parkingInfo: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "building_size")
    @Comment("건물 크기 (SMALL: 100㎡이하, MEDIUM: 100~500㎡, LARGE: 500㎡이상)")
    var buildingSize: BuildingSize? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "floating_population")
    @Comment("유동인구 수준 (VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH)")
    var floatingPopulation: FloatingPopulation? = null,

    @Column(name = "distance_to_station")
    @Comment("역과의 거리 (m)")
    var distanceToStation: Double? = null,

    @Column(name = "public_transport_users")
    @Comment("대중교통 이용객 수")
    var publicTransportUsers: Int? = null,

    @Column(name = "outlet_count")
    @Comment("매장 수")
    var outletCount: Int? = null
) : AbstractEntity() {

    // BuildingEntity에서 생명주기를 관리
    @OneToMany(mappedBy = "building", cascade = [CascadeType.ALL], orphanRemoval = true)
    @Comment("건물 통계 정보")
    var statistics: MutableList<BuildingStatisticEntity> = mutableListOf()

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            name: String,
            address: String? = null,
            areaId: Long? = null,
            regionId: Long? = null,
            latitude: Double? = null,
            longitude: Double? = null,
            totalFloorArea: Double? = null,
            hasElevator: Boolean? = null,
            parkingInfo: String? = null,
            buildingSize: BuildingSize? = null
        ): BuildingEntity {
            require(name.isNotBlank()) { "name must not be blank" }

            return BuildingEntity(
                id = id,
                name = name,
                address = address,
                areaId = areaId,
                regionId = regionId,
                latitude = latitude,
                longitude = longitude,
                totalFloorArea = totalFloorArea,
                hasElevator = hasElevator,
                parkingInfo = parkingInfo,
                buildingSize = buildingSize
            )
        }
    }
    
    /**
     * 도메인 모델로 변환
     */
    fun toDomain(): com.wheretopop.domain.building.Building {
        val location = latitude?.let { lat ->
            longitude?.let { lng ->
                Location.of(lat, lng)
            }
        }

        return com.wheretopop.domain.building.Building.create(
            id = id,
            name = name,
            address = address,
            areaId = areaId,
            regionId = regionId,
            location = location,
            totalFloorArea = totalFloorArea,
            hasElevator = hasElevator,
            parkingInfo = parkingInfo,
            buildingSize = buildingSize
        )
    }
    
    fun addStatistic(statistic: BuildingStatisticEntity) {
        statistics.add(statistic)
        statistic.building = this
    }
}
