package com.wheretopop.infrastructure.area.demographic

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역별 방문자 거주지 분포 정보 엔티티
 */
@Entity
@Table(
    name = "area_visitor_residence", 
    indexes = [
        Index(name = "idx_area_visitor_residence_area_id", columnList = "area_id"),
        Index(name = "idx_area_visitor_residence_location", columnList = "location")
    ]
)
@Comment("지역별 방문자 거주지 분포 정보 테이블")
class VisitorResidenceEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("방문자 거주지 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,

    @Column(name = "location", nullable = false)
    @Comment("거주지 위치 (시/구/동)")
    var location: String = "",

    @Column(name = "count", nullable = false)
    @Comment("해당 거주지 방문자 수")
    var count: Int = 0,

    @Column(name = "percentage", columnDefinition = "DECIMAL(5,2)", nullable = false)
    @Comment("전체 방문자 중 비율 (%)")
    var percentage: Double = 0.0,

) : AbstractEntity() 