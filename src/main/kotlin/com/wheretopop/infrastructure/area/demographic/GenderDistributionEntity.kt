package com.wheretopop.infrastructure.area.demographic

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.enums.Gender
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역별 성별 분포 정보 엔티티
 */
@Entity
@Table(
    name = "area_gender_distribution", 
    indexes = [
        Index(name = "idx_area_gender_distribution_area_id", columnList = "area_id"),
        Index(name = "idx_area_gender_distribution_gender", columnList = "gender")
    ]
)
@Comment("지역별 성별 분포 정보 테이블")
class GenderDistributionEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("성별 분포 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("성별")
    var gender: Gender,

    @Column(name = "count", nullable = false)
    @Comment("해당 성별 인구 수")
    var count: Int,

    @Column(name = "percentage", columnDefinition = "DECIMAL(5,2)", nullable = false)
    @Comment("전체 인구 중 비율 (%)")
    var percentage: Double,

) : AbstractEntity()