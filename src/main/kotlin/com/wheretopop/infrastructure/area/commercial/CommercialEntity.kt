package com.wheretopop.infrastructure.area.commercial

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역 상권 기본 정보 엔티티
 */
@Entity
@Table(
    name = "area_commercial_info",
    indexes = [
        Index(name = "idx_area_commercial_info_area_id", columnList = "area_id")
    ]
)
@Comment("지역별 상권 기본 정보 테이블")
class CommercialEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("상권 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,

    @Column(name = "total_stores", nullable = false)
    @Comment("전체 점포 수")
    var totalStores: Int = 0,

    @Column(name = "open_rate", columnDefinition = "DECIMAL(5,2)")
    @Comment("개업률 (%)")
    var openRate: Double? = null,

    @Column(name = "close_rate", columnDefinition = "DECIMAL(5,2)")
    @Comment("폐업률 (%)")
    var closeRate: Double? = null,

) : AbstractEntity() 