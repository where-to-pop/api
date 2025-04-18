package com.wheretopop.infrastructure.area.commercial

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역별 상점 카테고리 분포 정보 엔티티
 */
@Entity
@Table(
    name = "area_store_category", 
    indexes = [
        Index(name = "idx_area_store_category_area_id", columnList = "area_id"),
        Index(name = "idx_area_store_category_name", columnList = "name")
    ]
)
@Comment("지역별 상점 카테고리 분포 정보 테이블")
class StoreCategoryEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("카테고리 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,
    
    @Column(name = "name", nullable = false)
    @Comment("카테고리 이름")
    var name: String = "",
    
    @Column(name = "count", nullable = false)
    @Comment("해당 카테고리 매장 수")
    var count: Int = 0,
    
    @Column(name = "percentage", columnDefinition = "DECIMAL(5,2)")
    @Comment("전체 매장 중 비율 (%)")
    var percentage: Double? = null,
    
) : AbstractEntity() 