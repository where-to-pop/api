package com.wheretopop.infrastructure.building

import com.wheretopop.shared.enums.BuildingSize
import com.wheretopop.shared.enums.FloatingPopulation
import com.wheretopop.shared.model.AbstractEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "buildings")
@Comment("건물 정보 테이블")
class BuildingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Comment("건물 고유 식별자")
    var id: Long = 0,

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

    companion object {
        fun create(
            name: String,
            address: String? = null,
            areaId: Long? = null,
            regionId: Long? = null,
            totalFloorArea: Double? = null,
            hasElevator: Boolean? = null,
            parkingInfo: String? = null,
            buildingSize: BuildingSize? = null,
            floatingPopulation: FloatingPopulation? = null,
            distanceToStation: Double? = null,
            publicTransportUsers: Int? = null,
            outletCount: Int? = null
        ): BuildingEntity {
            require(name.isNotBlank()) { "name must not be blank" }

            return BuildingEntity(
                name = name,
                address = address,
                areaId = areaId,
                regionId = regionId,
                totalFloorArea = totalFloorArea,
                hasElevator = hasElevator,
                parkingInfo = parkingInfo,
                buildingSize = buildingSize,
                floatingPopulation = floatingPopulation,
                distanceToStation = distanceToStation,
                publicTransportUsers = publicTransportUsers,
                outletCount = outletCount
            )
        }
    }
}
