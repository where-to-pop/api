package com.wheretopop.infrastructure.area.external.opendata

import com.wheretopop.domain.area.AreaId
import com.wheretopop.shared.model.UniqueId
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


@Table("area_population")
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
    val congestionLevel: Int,

    @Column("congestion_message")
    val congestionMessage: String,

    @Column("ppltn_min")
    val ppltnMin: Int,

    @Column("ppltn_max")
    val ppltnMax: Int,

    @Column("male_ppltn_rate")
    val malePpltnRate: Double,

    @Column("female_ppltn_rate")
    val femalePpltnRate: Double,

    @Column("ppltn_rate_0")
    val ppltnRate0: Double,

    @Column("ppltn_rate_10")
    val ppltnRate10: Double,

    @Column("ppltn_rate_20")
    val ppltnRate20: Double,

    @Column("ppltn_rate_30")
    val ppltnRate30: Double,

    @Column("ppltn_rate_40")
    val ppltnRate40: Double,

    @Column("ppltn_rate_50")
    val ppltnRate50: Double,

    @Column("ppltn_rate_60")
    val ppltnRate60: Double,

    @Column("ppltn_rate_70")
    val ppltnRate70: Double,

    @Column("resnt_ppltn_rate")
    val resntPpltnRate: Double,

    @Column("non_resnt_ppltn_rate")
    val nonResntPpltnRate: Double,

    @Column("replace_yn")
    val replaceYn: Boolean,

    @Column("ppltn_time")
    val ppltnTime: Instant,

    @Column("fcst_yn")
    val fcstYn: Boolean,

    @Column("fcst_time")
    val fcstTime: Instant?,

    @Column("fcst_congestion_level")
    val fcstCongestionLevel: Int?,

    @Column("fcst_ppltn_min")
    val fcstPpltnMin: Int?,

    @Column("fcst_ppltn_max")
    val fcstPpltnMax: Int?,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),

    @Column("deleted_at")
    val deletedAt: Instant? = null
)

@WritingConverter
class AreaPopulationIdToLongConverter : Converter<AreaId, Long> {
    override fun convert(source: AreaId) = source.toLong()
}


@ReadingConverter
class LongToAreaPopulationIdConverter : Converter<Long, AreaId> {
    override fun convert(source: Long) = AreaId.of(source)
}
