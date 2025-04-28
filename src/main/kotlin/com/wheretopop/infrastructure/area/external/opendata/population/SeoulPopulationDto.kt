package com.wheretopop.infrastructure.area.external.opendata.population

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

/**
 * 서울시 실시간 인구 데이터 응답
 */
data class SeoulPopulationResponse(
    @JsonProperty("SeoulRtd.citydata_ppltn")
    val cityDataPopulation: List<CityDataPopulation>,
    
    @JsonProperty("RESULT")
    val result: ResultInfo? = null
)

/**
 * API 응답 결과 정보
 */
data class ResultInfo(
    @JsonProperty("RESULT.CODE")
    val code: String,
    
    @JsonProperty("RESULT.MESSAGE")
    val message: String
)

/**
 * String을 Boolean으로 변환하는 Deserializer
 */
class YnToBooleanDeserializer : JsonDeserializer<Boolean>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Boolean {
        return p.text == "Y"
    }
}

/**
 * String을 Instant로 변환하는 Deserializer
 */
class StringToInstantDeserializer : JsonDeserializer<Instant>() {
    companion object {
        private val FORMATTERS = listOf(
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm")
                .toFormatter()
                .withZone(ZoneId.systemDefault()),
            DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .toFormatter()
                .withZone(ZoneId.systemDefault())
        )
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
        val text = p.text
        
        // 각 포맷터로 파싱 시도
        for (formatter in FORMATTERS) {
            try {
                return try {
                    // LocalDateTime으로 파싱 시도
                    LocalDateTime.parse(text, formatter)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                } catch (e: Exception) {
                    // Instant로 직접 파싱 시도
                    Instant.from(formatter.parse(text))
                }
            } catch (e: Exception) {
                // 실패하면 다음 포맷터로 진행
                continue
            }
        }
        
        // 마지막 시도: 모든 포맷터가 실패한 경우
        throw IllegalArgumentException("Unable to parse date time: $text")
    }
}

/**
 * String을 Int로 변환하는 Deserializer
 */
class StringToIntDeserializer : JsonDeserializer<Int>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Int {
        return p.text.toInt()
    }
}

/**
 * String을 Int?로 변환하는 Deserializer
 */
class StringToNullableIntDeserializer : JsonDeserializer<Int?>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Int? {
        val text = p.text
        return if (text.isNullOrEmpty()) null else text.toIntOrNull()
    }
}

/**
 * String을 Instant?로 변환하는 Deserializer
 */
class StringToNullableInstantDeserializer : JsonDeserializer<Instant?>() {
    companion object {
        private val FORMATTERS = listOf(
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm")
                .toFormatter()
                .withZone(ZoneId.systemDefault()),
            DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .toFormatter()
                .withZone(ZoneId.systemDefault())
        )
    }
    
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant? {
        val text = p.text
        if (text.isNullOrEmpty()) return null
        
        // 각 포맷터로 파싱 시도
        for (formatter in FORMATTERS) {
            try {
                return try {
                    // LocalDateTime으로 파싱 시도
                    LocalDateTime.parse(text, formatter)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                } catch (e: Exception) {
                    // Instant로 직접 파싱 시도
                    Instant.from(formatter.parse(text))
                }
            } catch (e: Exception) {
                // 실패하면 다음 포맷터로 진행
                continue
            }
        }
        
        // 모든 포맷터가 실패한 경우
        return null
    }
}

/**
 * 하나의 핫스팟(지역) 데이터
 */
data class CityDataPopulation(
    @JsonProperty("AREA_NM")
    val areaName: String,

    @JsonProperty("AREA_CD")
    val areaCode: String,

    @JsonProperty("AREA_CONGEST_LVL")
    val congestionLevel: String,

    @JsonProperty("AREA_CONGEST_MSG")
    val congestionMessage: String,

    @JsonProperty("AREA_PPLTN_MIN")
    val populationMin: Int,

    @JsonProperty("AREA_PPLTN_MAX")
    val populationMax: Int,

    @JsonProperty("MALE_PPLTN_RATE")
    val malePopulationRate: Double,

    @JsonProperty("FEMALE_PPLTN_RATE")
    val femalePopulationRate: Double,

    @JsonProperty("PPLTN_RATE_0")
    val populationRate0: Double,

    @JsonProperty("PPLTN_RATE_10")
    val populationRate10: Double,

    @JsonProperty("PPLTN_RATE_20")
    val populationRate20: Double,

    @JsonProperty("PPLTN_RATE_30")
    val populationRate30: Double,

    @JsonProperty("PPLTN_RATE_40")
    val populationRate40: Double,

    @JsonProperty("PPLTN_RATE_50")
    val populationRate50: Double,

    @JsonProperty("PPLTN_RATE_60")
    val populationRate60: Double,

    @JsonProperty("PPLTN_RATE_70")
    val populationRate70: Double,

    @JsonProperty("RESNT_PPLTN_RATE")
    val residentPopulationRate: Double,

    @JsonProperty("NON_RESNT_PPLTN_RATE")
    val nonResidentPopulationRate: Double,

    @JsonProperty("REPLACE_YN")
    @JsonDeserialize(using = YnToBooleanDeserializer::class)
    val replaceYn: Boolean,

    @JsonProperty("PPLTN_TIME")
    @JsonDeserialize(using = StringToInstantDeserializer::class)
    val populationUpdateTime: Instant,

    @JsonProperty("FCST_YN")
    @JsonDeserialize(using = YnToBooleanDeserializer::class)
    val forecastYn: Boolean = false,

    @JsonProperty("FCST_PPLTN")
    val forecastPopulation: List<ForecastPopulation>? = null
) {
    /**
     * 혼잡도 문자열을 숫자로 변환합니다.
     * "여유" -> 1, "보통" -> 2, "약간 붐빔" -> 3, "붐빔" -> 4, "매우 붐빔" -> 5
     */
    fun getCongestionLevelAsInt(): Int {
        return when (congestionLevel) {
            "여유" -> 1
            "보통" -> 2
            "약간 붐빔" -> 3
            "붐빔" -> 4
            "매우 붐빔" -> 5
            else -> try {
                congestionLevel.toInt()
            } catch (e: Exception) {
                0 // 변환 실패시 기본값
            }
        }
    }
}

/**
 * 인구 예측 오브젝트
 */
data class ForecastPopulation(
    @JsonProperty("FCST_TIME")
    @JsonDeserialize(using = StringToInstantDeserializer::class)
    val forecastTime: Instant,
    
    @JsonProperty("FCST_CONGEST_LVL")
    val forecastCongestionLevel: String,
    
    @JsonProperty("FCST_PPLTN_MIN")
    val forecastPopulationMin: Int,
    
    @JsonProperty("FCST_PPLTN_MAX")
    val forecastPopulationMax: Int
) {
    /**
     * 예측 혼잡도 문자열을 숫자로 변환합니다.
     */
    fun getForecastCongestionLevelAsInt(): Int {
        return when (forecastCongestionLevel) {
            "여유" -> 1
            "보통" -> 2
            "약간 붐빔" -> 3
            "붐빔" -> 4
            "매우 붐빔" -> 5
            else -> try {
                forecastCongestionLevel.toInt()
            } catch (e: Exception) {
                0 // 변환 실패시 기본값
            }
        }
    }
}
