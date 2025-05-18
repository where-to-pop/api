package com.wheretopop.shared.infrastructure.entity

import com.fasterxml.jackson.core.type.TypeReference
import com.wheretopop.config.AreaIdConverter
import com.wheretopop.config.AreaPopulationIdConverter
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.infrastructure.area.external.opendata.population.CityDataPopulation
import com.wheretopop.infrastructure.area.external.opendata.population.ForecastPopulation
import com.wheretopop.shared.domain.identifier.AreaPopulationId
import com.wheretopop.shared.util.JsonUtil
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.time.Instant

/**
 * 지역 인구 정보(AreaPopulation) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "area_populations")
@EntityListeners(AuditingEntityListener::class)
class AreaPopulationEntity(
    @Id
    @JdbcTypeCode(Types.BIGINT)
    @Convert(converter = AreaPopulationIdConverter::class)
    val id: AreaPopulationId = AreaPopulationId.create(),

    @Column(name = "area_id", nullable = false)
    @JdbcTypeCode(Types.BIGINT)
    @Convert(converter = AreaIdConverter::class)
    val areaId: AreaId,

    @Column(name = "area_name", nullable = false)
    val areaName: String,

    @Column(name = "area_code", nullable = false)
    val areaCode: String,

    @Column(name = "congestion_level", nullable = false)
    val congestionLevel: String,

    @Column(name = "congestion_message", nullable = false)
    val congestionMessage: String,

    @Column(name = "population_min", nullable = false)
    val populationMin: Int,

    @Column(name = "population_max", nullable = false)
    val populationMax: Int,

    @Column(name = "male_population_rate", nullable = false)
    val malePopulationRate: Double,

    @Column(name = "female_population_rate", nullable = false)
    val femalePopulationRate: Double,

    @Column(name = "population_rate_0", nullable = false)
    val populationRate0: Double,

    @Column(name = "population_rate_10", nullable = false)
    val populationRate10: Double,

    @Column(name = "population_rate_20", nullable = false)
    val populationRate20: Double,

    @Column(name = "population_rate_30", nullable = false)
    val populationRate30: Double,

    @Column(name = "population_rate_40", nullable = false)
    val populationRate40: Double,

    @Column(name = "population_rate_50", nullable = false)
    val populationRate50: Double,

    @Column(name = "population_rate_60", nullable = false)
    val populationRate60: Double,

    @Column(name = "population_rate_70", nullable = false)
    val populationRate70: Double,

    @Column(name = "resident_population_rate", nullable = false)
    val residentPopulationRate: Double,

    @Column(name = "non_resident_population_rate", nullable = false)
    val nonResidentPopulationRate: Double,

    @Column(name = "replace_yn", nullable = false)
    val replaceYn: Boolean,

    @Column(name = "population_update_time", nullable = false)
    val populationUpdateTime: Instant,

    @Column(name = "forecast_yn", nullable = false)
    val forecastYn: Boolean,

    @Column(name = "forecast_population_json", columnDefinition = "TEXT")
    val forecastPopulationJson: String?,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
    val deletedAt: Instant? = null
) {
    /**
     * JSON 문자열에서 ForecastPopulation 객체 리스트로 변환
     */
    fun getForecastPopulations(): List<ForecastPopulation>? {
        if (forecastPopulationJson.isNullOrBlank()) return null
        
        return try {
            JsonUtil.objectMapper.readValue(
                forecastPopulationJson,
                object : TypeReference<List<ForecastPopulation>>() {}
            )
        } catch (e: Exception) {
            null
        }
    }
    
    companion object {
        fun from(cityDataPopulation: CityDataPopulation, areaId: AreaId): AreaPopulationEntity {
            val forecastPopulationJson = cityDataPopulation.forecastPopulation?.let {
                JsonUtil.objectMapper.writeValueAsString(it)
            }
            
            return AreaPopulationEntity(
                id = AreaPopulationId.create(),
                areaId = areaId,
                areaName = cityDataPopulation.areaName,
                areaCode = cityDataPopulation.areaCode,
                congestionLevel = cityDataPopulation.congestionLevel,
                congestionMessage = cityDataPopulation.congestionMessage,
                populationMin = cityDataPopulation.populationMin,
                populationMax = cityDataPopulation.populationMax,
                malePopulationRate = cityDataPopulation.malePopulationRate,
                femalePopulationRate = cityDataPopulation.femalePopulationRate,
                populationRate0 = cityDataPopulation.populationRate0,
                populationRate10 = cityDataPopulation.populationRate10,
                populationRate20 = cityDataPopulation.populationRate20,
                populationRate30 = cityDataPopulation.populationRate30,
                populationRate40 = cityDataPopulation.populationRate40,
                populationRate50 = cityDataPopulation.populationRate50,
                populationRate60 = cityDataPopulation.populationRate60,
                populationRate70 = cityDataPopulation.populationRate70,
                residentPopulationRate = cityDataPopulation.residentPopulationRate,
                nonResidentPopulationRate = cityDataPopulation.nonResidentPopulationRate,
                replaceYn = cityDataPopulation.replaceYn,
                populationUpdateTime = cityDataPopulation.populationUpdateTime,
                forecastYn = cityDataPopulation.forecastYn,
                forecastPopulationJson = forecastPopulationJson
            )
        }
    }
}
