package com.wheretopop.infrastructure.brand

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 특정 지역의 브랜드 분포 정보 엔티티
 */
@Entity
@Table(name = "area_brand_distribution", 
    indexes = [
        Index(name = "idx_area_brand_distribution_area_id", columnList = "area_id"),
        Index(name = "idx_area_brand_distribution_brand_id", columnList = "brand_id")
    ]
)
@Comment("지역별 브랜드 분포 정보 테이블")
class BrandDistributionEntity : AbstractEntity {
    @Id
    @Column(name = "id", nullable = false)
    @Comment("브랜드 분포 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create()
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    lateinit var area: AreaEntity
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    @Comment("브랜드 정보")
    lateinit var brand: BrandEntity
    
    @Column(name = "count", nullable = false)
    @Comment("해당 브랜드 매장 수")
    var count: Int = 0
    
    @Column(name = "percentage", columnDefinition = "DECIMAL(5,2)")
    @Comment("전체 브랜드 중 비율 (%)")
    var percentage: Double? = null
    
    @Column(name = "collected_at", nullable = false)
    @Comment("정보 수집 시간")
    var collectedAt: LocalDateTime = LocalDateTime.now()
    
    /**
     * 생성자 (JPA 명세상 기본 생성자로도 사용 가능)
     */
    constructor(
        id: UniqueId = UniqueId.create(),
        area: AreaEntity? = null,
        brand: BrandEntity? = null,
        count: Int = 0,
        percentage: Double? = null,
        collectedAt: LocalDateTime = LocalDateTime.now()
    ) : super() {
        this.id = id
        area?.let { this.area = it }
        brand?.let { this.brand = it }
        this.count = count
        this.percentage = percentage
        this.collectedAt = collectedAt
    }
} 