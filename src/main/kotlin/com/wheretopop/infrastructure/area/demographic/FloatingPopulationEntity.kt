package com.wheretopop.infrastructure.area.demographic

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 지역별 유동인구 정보 엔티티
 */
@Entity
@Table(
    name = "area_floating_population", 
    indexes = [
        Index(name = "idx_area_floating_population_area_id", columnList = "area_id")
    ]
)
@Comment("지역별 유동인구 정보 테이블")
class FloatingPopulationEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("유동인구 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    var id: UniqueId = UniqueId.create(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,

    @Column(name = "daily_average", nullable = false)
    @Comment("일평균 유동인구 수")
    var dailyAverage: Int = 0,
    
    @Column(name = "population_density", nullable = false)
    @Comment("인구 밀도 (명/km²)")
    var populationDensity: Int = 0,
    
    @Column(name = "weekday_peak_hour")
    @Comment("평일 최고 시간대")
    var weekdayPeakHour: String? = null,
    
    @Column(name = "weekday_peak_population")
    @Comment("평일 최고 시간대 유동인구")
    var weekdayPeakPopulation: Int? = null,
    
    @Column(name = "weekend_peak_hour")
    @Comment("주말 최고 시간대")
    var weekendPeakHour: String? = null,
    
    @Column(name = "weekend_peak_population")
    @Comment("주말 최고 시간대 유동인구")
    var weekendPeakPopulation: Int? = null,
    
    @Column(name = "morning_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("오전 유동인구 비율 (%)")
    var morningRatio: Double? = null,
    
    @Column(name = "afternoon_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("오후 유동인구 비율 (%)")
    var afternoonRatio: Double? = null,
    
    @Column(name = "evening_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("저녁 유동인구 비율 (%)")
    var eveningRatio: Double? = null,
    
    @Column(name = "night_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("심야 유동인구 비율 (%)")
    var nightRatio: Double? = null,

) : AbstractEntity() 