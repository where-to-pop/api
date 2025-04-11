package com.wheretopop.infrastructure.area

import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import com.wheretopop.shared.model.Location


@Entity
@Table(name = "areas", indexes = [Index(name = "idx_area_region_id", columnList = "region_id")])
@Comment("권역 정보 테이블")
class AreaEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("권역 고유 식별자 (Snowflake ID)")
    @JdbcTypeCode(SqlTypes.BIGINT)
    var id: UniqueId = UniqueId.create(),

    @Column(name = "name", nullable = false)
    @Comment("권역 이름")
    var name: String,

    @Column(name = "description")
    @Comment("권역 설명")
    var description: String? = null,

    @Column(name = "latitude")
    @Comment("위도")
    var latitude: Double? = null,

    @Column(name = "longitude")
    @Comment("경도")
    var longitude: Double? = null,

    @Column(name = "region_id", nullable = true)
    @Comment("지역 ID (FK - regions 테이블)(논리적 fk)")
    var regionId: Long? = null,

    @Column(name = "main_business_types")
    @Comment("주요 비즈니스 타입 (콤마로 구분)")
    var mainBusinessTypes: String? = null
) : AbstractEntity() {
    
    // AreaEntity에서 생명주기를 관리
    @OneToMany(mappedBy = "area", cascade = [CascadeType.ALL], orphanRemoval = true)
    @Comment("권역 통계 정보")
    var statistics: MutableList<AreaStatisticEntity> = mutableListOf()

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            name: String,
            description: String? = null,
            latitude: Double? = null,
            longitude: Double? = null,
            regionId: Long? = null,
        ): AreaEntity {
            require(name.isNotBlank()) { "name must not be blank" }

            return AreaEntity(
                id = id,
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude,
                regionId = regionId,
            )
        }
    }
    
    
    fun addStatistic(statistic: AreaStatisticEntity) {
        statistics.add(statistic)
        statistic.area = this
    }
}
