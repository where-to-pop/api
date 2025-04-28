package com.wheretopop.infrastructure.area.external.opendata.population

import com.fasterxml.jackson.core.type.TypeReference
import com.wheretopop.domain.area.AreaId
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.util.JsonUtil
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

class AreaPopulationId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): AreaPopulationId {
            return AreaPopulationId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): AreaPopulationId {
            return AreaPopulationId(UniqueId.of(value).value)
        }
    }
}


@Table("area_populations")
data class AreaPopulationEntity(
    @Id
    @Column("id")
    val id: AreaPopulationId = AreaPopulationId.create(),

    @Column("area_id") // FK
    val areaId: AreaId,

    @Column("area_name")
    val areaName: String,

    @Column("area_code")
    val areaCode: String,

    @Column("congestion_level")
    val congestionLevel: String,

    @Column("congestion_message")
    val congestionMessage: String,

    @Column("population_min")
    val populationMin: Int,

    @Column("population_max")
    val populationMax: Int,

    @Column("male_population_rate")
    val malePopulationRate: Double,

    @Column("female_population_rate")
    val femalePopulationRate: Double,

    @Column("population_rate_0")
    val populationRate0: Double,

    @Column("population_rate_10")
    val populationRate10: Double,

    @Column("population_rate_20")
    val populationRate20: Double,

    @Column("population_rate_30")
    val populationRate30: Double,

    @Column("population_rate_40")
    val populationRate40: Double,

    @Column("population_rate_50")
    val populationRate50: Double,

    @Column("population_rate_60")
    val populationRate60: Double,

    @Column("population_rate_70")
    val populationRate70: Double,

    @Column("resident_population_rate")
    val residentPopulationRate: Double,

    @Column("non_resident_population_rate")
    val nonResidentPopulationRate: Double,

    @Column("replace_yn")
    val replaceYn: Boolean,

    @Column("population_update_time")
    val populationUpdateTime: Instant,

    @Column("forecast_yn")
    val forecastYn: Boolean,

    @Column("forecast_population_json")
    val forecastPopulationJson: String?,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),

    @Column("deleted_at")
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
        fun of(cityDataPopulation: CityDataPopulation, areaId: AreaId): AreaPopulationEntity {
            // ForecastPopulation 리스트를 JSON 문자열로 변환
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
                forecastPopulationJson = forecastPopulationJson,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null
            )
        }
    }
}

@WritingConverter
class AreaPopulationIdToLongConverter : Converter<AreaPopulationId, Long> {
    override fun convert(source: AreaPopulationId) = source.toLong()
}

@ReadingConverter
class LongToAreaPopulationIdConverter : Converter<Long, AreaPopulationId> {
    override fun convert(source: Long) = AreaPopulationId.of(source)
}
