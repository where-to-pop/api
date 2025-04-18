package com.wheretopop.infrastructure.area.realestate

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역별 부동산 정보 엔티티
 */
@Entity
@Table(
    name = "area_real_estate", 
    indexes = [
        Index(name = "idx_area_real_estate_area_id", columnList = "area_id")
    ]
)
@Comment("지역별 부동산 정보 테이블")
class RealEstateEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("부동산 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,

    @Column(name = "average_rent")
    @Comment("평균 임대료 (원/m²)")
    var averageRent: Long? = null,

    @Column(name = "average_vacancy_rate", columnDefinition = "DECIMAL(5,2)")
    @Comment("평균 공실률 (%)")
    var averageVacancyRate: Double? = null,
    
    @Column(name = "min_rent")
    @Comment("최소 임대료 (원/m²)")
    var minRent: Long? = null,
    
    @Column(name = "max_rent")
    @Comment("최대 임대료 (원/m²)")
    var maxRent: Long? = null,
    
    @Column(name = "recent_price_trend", columnDefinition = "DECIMAL(5,2)")
    @Comment("최근 가격 추세 (3개월 변화율, %)")
    var recentPriceTrend: Double? = null,
    
    @Column(name = "building_count")
    @Comment("건물 수")
    var buildingCount: Int? = null,
    
    @Column(name = "average_building_age", columnDefinition = "DECIMAL(5,1)")
    @Comment("평균 건물 연식 (년)")
    var averageBuildingAge: Double? = null,

) : AbstractEntity() 