package com.wheretopop.infrastructure.area.external.opendata.population

import com.wheretopop.domain.area.Area
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * 인구 데이터 변환을 위한 매퍼 클래스
 */
@Component
class AreaPopulationMapper {
    
    /**
     * 서울시 API 응답에서 인구 데이터 정보를 추출합니다.
     */
    fun mapToPopulationData(response: SeoulPopulationResponse): AreaPopulationData? {
        // 응답에서 데이터 추출
        val citydataResponse = response.citydata_ppltn ?: return null
        val rowList = citydataResponse.row ?: return null
        if (rowList.isEmpty()) return null
        
        // 첫 번째 데이터만 사용 (한 장소에 대한 데이터만 요청하므로)
        val cityData = rowList.first()
        val status = cityData.LIVE_PPLTN_STTS ?: return null
        
        return AreaPopulationData(
            areaName = cityData.AREA_NM ?: "",
            areaCode = cityData.AREA_CD ?: cityData.AREA_NM ?: "",
            congestionLevel = status.AREA_CONGEST_LVL?.toIntOrNull() ?: 0,
            congestionMessage = status.AREA_CONGEST_MSG ?: "",
            ppltnMin = status.PPLTN_MIN?.toIntOrNull() ?: 0,
            ppltnMax = status.PPLTN_MAX?.toIntOrNull() ?: 0,
            malePpltnRate = status.MALE_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
            femalePpltnRate = status.FEMALE_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
            ppltnRate0 = status.PPLTN_RATE_0?.toDoubleOrNull() ?: 0.0,
            ppltnRate10 = status.PPLTN_RATE_10?.toDoubleOrNull() ?: 0.0,
            ppltnRate20 = status.PPLTN_RATE_20?.toDoubleOrNull() ?: 0.0,
            ppltnRate30 = status.PPLTN_RATE_30?.toDoubleOrNull() ?: 0.0,
            ppltnRate40 = status.PPLTN_RATE_40?.toDoubleOrNull() ?: 0.0,
            ppltnRate50 = status.PPLTN_RATE_50?.toDoubleOrNull() ?: 0.0,
            ppltnRate60 = status.PPLTN_RATE_60?.toDoubleOrNull() ?: 0.0,
            ppltnRate70 = status.PPLTN_RATE_70?.toDoubleOrNull() ?: 0.0,
            resntPpltnRate = status.RESNT_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
            nonResntPpltnRate = status.NON_RESNT_PPLTN_RATE?.toDoubleOrNull() ?: 0.0,
            replaceYn = status.REPLACE_YN == "Y",
            ppltnTime = status.PPLTN_TIME ?: Instant.now().toString(),
            fcstYn = status.FCST_YN == "Y",
            fcstTime = status.FCST_PPLTN_TIME,
            fcstCongestionLevel = status.FCST_CONGEST_LVL?.toIntOrNull(),
            fcstPpltnMin = status.FCST_PPLTN_MIN?.toIntOrNull(),
            fcstPpltnMax = status.FCST_PPLTN_MAX?.toIntOrNull()
        )
    }
    
    /**
     * DTO에서 Entity로 변환합니다.
     */
    fun mapToEntity(data: AreaPopulationData, area: Area): AreaPopulationEntity {
        return AreaPopulationEntity(
            areaId = area.id,
            areaName = area.name,
            areaCode = data.areaCode,
            congestionLevel = data.congestionLevel,
            congestionMessage = data.congestionMessage,
            ppltnMin = data.ppltnMin,
            ppltnMax = data.ppltnMax,
            malePpltnRate = data.malePpltnRate,
            femalePpltnRate = data.femalePpltnRate,
            ppltnRate0 = data.ppltnRate0,
            ppltnRate10 = data.ppltnRate10,
            ppltnRate20 = data.ppltnRate20,
            ppltnRate30 = data.ppltnRate30,
            ppltnRate40 = data.ppltnRate40,
            ppltnRate50 = data.ppltnRate50,
            ppltnRate60 = data.ppltnRate60,
            ppltnRate70 = data.ppltnRate70,
            resntPpltnRate = data.resntPpltnRate,
            nonResntPpltnRate = data.nonResntPpltnRate,
            replaceYn = data.replaceYn,
            ppltnTime = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(data.ppltnTime)),
            fcstYn = data.fcstYn,
            fcstTime = data.fcstTime?.let { Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it)) },
            fcstCongestionLevel = data.fcstCongestionLevel,
            fcstPpltnMin = data.fcstPpltnMin,
            fcstPpltnMax = data.fcstPpltnMax
        )
    }
} 