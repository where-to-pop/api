package com.wheretopop.infrastructure.area.demographic

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역별 교통 수단 이용 비율 정보 엔티티
 */
@Entity
@Table(
    name = "area_transportation_usage", 
    indexes = [
        Index(name = "idx_area_transportation_area_id", columnList = "area_id"),
        Index(name = "idx_area_transportation_type", columnList = "transportation_type")
    ]
)
@Comment("지역별 교통 수단 이용 비율 정보 테이블")
class TransportationUsageEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("교통 수단 이용 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,

    @Column(name = "transportation_type", nullable = false)
    @Comment("교통 수단 유형 (지하철, 버스, 자가용 등)")
    var transportationType: String,

    @Column(name = "count", nullable = false)
    @Comment("해당 교통 수단 이용자 수")
    var count: Int,

    @Column(name = "percentage", columnDefinition = "DECIMAL(5,2)", nullable = false)
    @Comment("전체 이용자 중 비율 (%)")
    var percentage: Double,


) : AbstractEntity() 