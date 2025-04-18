package com.wheretopop.infrastructure.area.demographic

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역별 연령 분포 정보 엔티티
 */
@Entity
@Table(
    name = "area_age_distribution", 
    indexes = [
        Index(name = "idx_area_age_distribution_area_id", columnList = "area_id"),
        Index(name = "idx_area_age_distribution_age_group", columnList = "age_group")
    ]
)
@Comment("지역별 연령 분포 정보 테이블")
class AgeDistributionEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("연령 분포 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,

    @Column(name = "age_group", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("연령대")
    var ageGroup: AgeGroup,

    @Column(name = "count", nullable = false)
    @Comment("해당 연령대 인구 수")
    var count: Int,

    @Column(name = "percentage", columnDefinition = "DECIMAL(5,2)", nullable = false)
    @Comment("전체 인구 중 비율 (%)")
    var percentage: Double,

) : AbstractEntity()