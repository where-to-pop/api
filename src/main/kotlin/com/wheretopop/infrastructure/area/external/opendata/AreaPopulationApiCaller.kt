package com.wheretopop.infrastructure.area.external.opendata

import com.wheretopop.infrastructure.area.AreaRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.Instant
import java.time.format.DateTimeFormatter


@Component
class AreaPopulationApiCaller(
    private val webClient: WebClient,
    private val areaPopulationRepository: AreaPopulationRepository,
    private val areaRepository: AreaRepository,
    @Value("\${openapi.seoul.key}") private val apiKey: String
) : OpenDataApiCaller {

    override fun support(type: OpenDataType): Boolean {
        return type == OpenDataType.AREA_POPULATION
    }

    override suspend fun callAndSave() {
        // 서울시 모든 지역에 대해 데이터 수집
        val areas = areaRepository.findAll()
        
        for (area in areas) {
            try {
                // API 호출
                val response = webClient.get()
                    .uri { builder ->
                        builder.path("/{key}/json/citydata_ppltn/1/5/{areaNm}")
                            .build(apiKey, area.name)
                    }
                    .retrieve()
                    .awaitBody<SeoulPopulationResponse>()
                
                // 응답에서 필요한 데이터 추출
                val populationData = response.CityData?.getPopulationInfo()
                if (populationData != null) {
                    val entity = AreaPopulationEntity(
                        areaId = area.id,
                        areaName = area.name,
                        areaCode = populationData.areaCode ?: area.name,
                        congestionLevel = populationData.congestionLevel,
                        congestionMessage = populationData.congestionMessage,
                        ppltnMin = populationData.ppltnMin,
                        ppltnMax = populationData.ppltnMax,
                        malePpltnRate = populationData.malePpltnRate,
                        femalePpltnRate = populationData.femalePpltnRate,
                        ppltnRate0 = populationData.ppltnRate0,
                        ppltnRate10 = populationData.ppltnRate10,
                        ppltnRate20 = populationData.ppltnRate20,
                        ppltnRate30 = populationData.ppltnRate30,
                        ppltnRate40 = populationData.ppltnRate40,
                        ppltnRate50 = populationData.ppltnRate50,
                        ppltnRate60 = populationData.ppltnRate60,
                        ppltnRate70 = populationData.ppltnRate70,
                        resntPpltnRate = populationData.resntPpltnRate,
                        nonResntPpltnRate = populationData.nonResntPpltnRate,
                        replaceYn = populationData.replaceYn,
                        ppltnTime = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(populationData.ppltnTime)),
                        fcstYn = populationData.fcstYn,
                        fcstTime = populationData.fcstTime?.let { Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it)) },
                        fcstCongestionLevel = populationData.fcstCongestionLevel,
                        fcstPpltnMin = populationData.fcstPpltnMin,
                        fcstPpltnMax = populationData.fcstPpltnMax
                    )
                    
                    areaPopulationRepository.save(entity)
                    logger.info { "Population data saved for area: ${area.name}" }
                } else {
                    logger.warn { "No population data available for area: ${area.name}" }
                }
            } catch (e: Exception) {
                // 에러 로깅하고 계속 진행
                logger.error(e) { "Error fetching population data for ${area.name}: ${e.message}" }
            }
        }
    }
}

// 서울시 실시간 인구데이터 API 응답 모델
data class SeoulPopulationResponse(
    val CityData: CityData?
)

data class CityData(
    val AREA_NM: String?,
    val LIVE_PPLTN_STTS: LivePopulationStatus?
) {
    fun getPopulationInfo(): AreaPopulationData? {
        return LIVE_PPLTN_STTS?.let {
            AreaPopulationData(
                areaName = AREA_NM ?: "",
                areaCode = AREA_NM ?: "",
                congestionLevel = it.AREA_CONGEST_LVL?.toIntOrNull() ?: 0,
                congestionMessage = it.AREA_CONGEST_MSG ?: "",
                ppltnMin = it.PPLTN_MIN?.toIntOrNull() ?: 0,
                ppltnMax = it.PPLTN_MAX?.toIntOrNull() ?: 0,
                malePpltnRate = it.MALE_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
                femalePpltnRate = it.FEMALE_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
                ppltnRate0 = it.PPLTN_RATE_0?.toDoubleOrNull() ?: 0.0,
                ppltnRate10 = it.PPLTN_RATE_10?.toDoubleOrNull() ?: 0.0,
                ppltnRate20 = it.PPLTN_RATE_20?.toDoubleOrNull() ?: 0.0,
                ppltnRate30 = it.PPLTN_RATE_30?.toDoubleOrNull() ?: 0.0,
                ppltnRate40 = it.PPLTN_RATE_40?.toDoubleOrNull() ?: 0.0,
                ppltnRate50 = it.PPLTN_RATE_50?.toDoubleOrNull() ?: 0.0,
                ppltnRate60 = it.PPLTN_RATE_60?.toDoubleOrNull() ?: 0.0,
                ppltnRate70 = it.PPLTN_RATE_70?.toDoubleOrNull() ?: 0.0,
                resntPpltnRate = it.RESNT_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
                nonResntPpltnRate = it.NON_RESNT_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
                replaceYn = it.REPLACE_YN == "Y",
                ppltnTime = it.PPLTN_TIME ?: Instant.now().toString(),
                fcstYn = it.FCST_YN == "Y",
                fcstTime = it.FCST_PPLTN_TIME,
                fcstCongestionLevel = it.FCST_CONGEST_LVL?.toIntOrNull(),
                fcstPpltnMin = it.FCST_PPLTN_MIN?.toIntOrNull(),
                fcstPpltnMax = it.FCST_PPLTN_MAX?.toIntOrNull()
            )
        }
    }
}

data class LivePopulationStatus(
    val AREA_CONGEST_LVL: String?,
    val AREA_CONGEST_MSG: String?,
    val PPLTN_MIN: String?,
    val PPLTN_MAX: String?,
    val MALE_PPLTN_RATE: String?,
    val FEMALE_PPLTN_RATE: String?,
    val PPLTN_RATE_0: String?,
    val PPLTN_RATE_10: String?,
    val PPLTN_RATE_20: String?,
    val PPLTN_RATE_30: String?,
    val PPLTN_RATE_40: String?,
    val PPLTN_RATE_50: String?,
    val PPLTN_RATE_60: String?,
    val PPLTN_RATE_70: String?,
    val RESNT_PPLTN_RATE: String?,
    val NON_RESNT_PPLTN_RATE: String?,
    val REPLACE_YN: String?,
    val PPLTN_TIME: String?,
    val FCST_YN: String?,
    val FCST_PPLTN_TIME: String?,
    val FCST_CONGEST_LVL: String?,
    val FCST_PPLTN_MIN: String?,
    val FCST_PPLTN_MAX: String?
)

// DTO 모델 (유지)
data class AreaPopulationData(
    val areaName: String,
    val areaCode: String,
    val congestionLevel: Int,
    val congestionMessage: String,
    val ppltnMin: Int,
    val ppltnMax: Int,
    val malePpltnRate: Double,
    val femalePpltnRate: Double,
    val ppltnRate0: Double,
    val ppltnRate10: Double,
    val ppltnRate20: Double,
    val ppltnRate30: Double,
    val ppltnRate40: Double,
    val ppltnRate50: Double,
    val ppltnRate60: Double,
    val ppltnRate70: Double,
    val resntPpltnRate: Double,
    val nonResntPpltnRate: Double,
    val replaceYn: Boolean,
    val ppltnTime: String,
    val fcstYn: Boolean,
    val fcstTime: String?,
    val fcstCongestionLevel: Int?,
    val fcstPpltnMin: Int?,
    val fcstPpltnMax: Int?
)

private val logger = KotlinLogging.logger {}
