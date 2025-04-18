package com.wheretopop.infrastructure.building

import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

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

    @Column(name = "address", nullable = false)
    @Comment("건물 주소")
    var address: String,

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

    @Column(name = "building_size")
    @Comment("건물 크기 (m²)")
    var buildingSize: Double? = null,

    @Column(name = "distance_to_station")
    @Comment("역과의 거리 (m)")
    var distanceToStation: Double? = null,

    @Column(name = "public_transport_users")
    @Comment("대중교통 이용객 수")
    var publicTransportUsers: Int? = null,
) : AbstractEntity() {

    protected constructor() : this(
        id = UniqueId.create(),
        name = "",
        address = ""
    )

    // BuildingEntity에서 생명주기를 관리
    @OneToMany(mappedBy = "building", cascade = [CascadeType.ALL], orphanRemoval = true)
    @Comment("건물 통계 정보")
    var statistics: MutableList<BuildingStatisticEntity> = mutableListOf()

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            name: String,
            address: String,
            areaId: Long? = null,
            regionId: Long? = null,
            latitude: Double? = null,
            longitude: Double? = null,
            totalFloorArea: Double? = null,
            hasElevator: Boolean? = null,
            parkingInfo: String? = null,
            buildingSize: Double? = null
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
}
