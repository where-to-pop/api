package com.wheretopop.infrastructure.area.external.opendata.population

/**
 * 서울시 실시간 인구데이터 API 응답 모델
 * 참조: https://data.seoul.go.kr/dataList/OA-21778/A/1/datasetView.do
 */
data class SeoulPopulationResponse(
    val citydata_ppltn: CitydataResponse?
)

data class CitydataResponse(
    val list_total_count: Int?,   // 전체 결과 수
    val RESULT: Result?,          // 요청 결과
    val row: List<CityData>?      // 결과 데이터 목록
)

data class Result(
    val CODE: String?,            // 결과 코드
    val MESSAGE: String?          // 결과 메시지
)

data class CityData(
    val AREA_NM: String?,         // 지역명
    val AREA_CD: String?,         // 지역코드  
    val LIVE_PPLTN_STTS: LivePopulationStatus? // 실시간 인구 현황
)

data class LivePopulationStatus(
    val AREA_CONGEST_LVL: String?,     // 혼잡도 수준 (1-4)
    val AREA_CONGEST_MSG: String?,     // 혼잡도 메시지
    val PPLTN_TIME: String?,           // 측정 시간
    val PPLTN_MIN: String?,            // 인구 수 최소값
    val PPLTN_MAX: String?,            // 인구 수 최대값  
    val MALE_PPLTN_RATE: String?,      // 남성 비율
    val FEMALE_PPLTN_RATE: String?,    // 여성 비율
    val PPLTN_RATE_0: String?,         // 10세 미만 비율
    val PPLTN_RATE_10: String?,        // 10대 비율
    val PPLTN_RATE_20: String?,        // 20대 비율
    val PPLTN_RATE_30: String?,        // 30대 비율
    val PPLTN_RATE_40: String?,        // 40대 비율
    val PPLTN_RATE_50: String?,        // 50대 비율
    val PPLTN_RATE_60: String?,        // 60대 비율
    val PPLTN_RATE_70: String?,        // 70대 이상 비율
    val RESNT_PPLTN_RATE: String?,     // 상주인구 비율
    val NON_RESNT_PPLTN_RATE: String?, // 비상주인구 비율
    val REPLACE_YN: String?,           // 데이터 대체 여부 (Y/N)
    val FCST_YN: String?,              // 예측 데이터 여부 (Y/N)
    val FCST_PPLTN_TIME: String?,      // 예측 시간
    val FCST_CONGEST_LVL: String?,     // 예측 혼잡도 수준
    val FCST_PPLTN_MIN: String?,       // 예측 인구 수 최소값
    val FCST_PPLTN_MAX: String?        // 예측 인구 수 최대값
)

/**
 * 변환된 인구데이터 DTO (비즈니스 로직용)
 */
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