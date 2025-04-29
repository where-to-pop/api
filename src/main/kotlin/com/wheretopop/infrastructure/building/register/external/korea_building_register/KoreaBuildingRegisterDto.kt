package com.wheretopop.infrastructure.building.register.external.korea_building_register

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wheretopop.infrastructure.area.external.opendata.population.StringToInstantDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

/**
 * 한국 건축물 대장 데이터 응답
 */
data class KoreaBuildingRegisterResponse(
    @JsonProperty("header")
    val result: Header? = null,

    @JsonProperty("body")
    val body: Body
)

/**
 * API 응답 결과 정보
 */
data class Header(
    @JsonProperty("header.resultCode")
    val resultCode: String,
    
    @JsonProperty("header.resultMsg")
    val resultMsg: String
)

data class Body(
    @JsonProperty("body.items")
    val items: List<Item>,

    @JsonProperty("body.numOfRows")
    val numOfRows: String,

    @JsonProperty("body.pageNo")
    val pageNo: String,

    @JsonProperty("body.totalCount")
    val totalCount: String,
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
 * 하나의 건축물대장 데이터
 */
data class Item(
    // 주용도코드명
    @JsonProperty("mainPurpsCdNm")
    val mainPurpsCdNm: String? = null,

    // 기타용도
    @JsonProperty("etcPurps")
    val etcPurps: String? = null,

    // 지붕코드
    @JsonProperty("roofCd")
    val roofCd: String? = null,

    // 지붕코드명
    @JsonProperty("roofCdNm")
    val roofCdNm: String? = null,

    // 기타지붕
    @JsonProperty("etcRoof")
    val etcRoof: String? = null,

    // 세대수(세대)
    @JsonProperty("hhldCnt")
    val hhldCnt: Int? = null,

    // 가구수(가구)
    @JsonProperty("fmlyCnt")
    val fmlyCnt: Int? = null,

    // 높이(m)
    @JsonProperty("heit")
    val heit: Double? = null,

    // 지상층수
    @JsonProperty("grndFlrCnt")
    val grndFlrCnt: Int? = null,

    // 지하층수
    @JsonProperty("ugrndFlrCnt")
    val ugrndFlrCnt: Int? = null,

    // 승용승강기수
    @JsonProperty("rideUseElvtCnt")
    val rideUseElvtCnt: Int? = null,

    // 비상용승강기수
    @JsonProperty("emgenUseElvtCnt")
    val emgenUseElvtCnt: Int? = null,

    // 부속건축물수
    @JsonProperty("atchBldCnt")
    val atchBldCnt: Int? = null,

    // 부속건축물면적(㎡)
    @JsonProperty("atchBldArea")
    val atchBldArea: Double? = null,

    // 총동연면적(㎡)
    @JsonProperty("totDongTotArea")
    val totDongTotArea: Double? = null,

    // 옥내기계식대수(대)
    @JsonProperty("indrMechUtcnt")
    val indrMechUtcnt: Int? = null,

    // 옥내기계식면적(㎡)
    @JsonProperty("indrMechArea")
    val indrMechArea: Double? = null,

    // 옥외기계식대수(대)
    @JsonProperty("oudrMechUtcnt")
    val oudrMechUtcnt: Int? = null,

    // 옥외기계식면적(㎡)
    @JsonProperty("oudrMechArea")
    val oudrMechArea: Double? = null,

    // 옥내자주식대수(대)
    @JsonProperty("indrAutoUtcnt")
    val indrAutoUtcnt: Int? = null,

    // 옥내자주식면적(㎡)
    @JsonProperty("indrAutoArea")
    val indrAutoArea: Double? = null,

    // 옥외자주식대수(대)
    @JsonProperty("oudrAutoUtcnt")
    val oudrAutoUtcnt: Int? = null,

    // 옥외자주식면적(㎡)
    @JsonProperty("oudrAutoArea")
    val oudrAutoArea: Double? = null,

    // 허가일
    @JsonProperty("pmsDay")
    @JsonDeserialize(using = StringToInstantDeserializer::class)
    val pmsDay: Instant? = null,

    // 착공일
    @JsonProperty("stcnsDay")
    @JsonDeserialize(using = StringToInstantDeserializer::class)
    val stcnsDay: Instant? = null,

    // 사용승인일
    @JsonProperty("useAprDay")
    @JsonDeserialize(using = StringToInstantDeserializer::class)
    val useAprDay: Instant? = null,

    // 허가번호년
    @JsonProperty("pmsnoYear")
    val pmsnoYear: Int? = null,

    // 허가번호기관코드
    @JsonProperty("pmsnoKikCd")
    val pmsnoKikCd: String? = null,

    // 허가번호기관코드명
    @JsonProperty("pmsnoKikCdNm")
    val pmsnoKikCdNm: String? = null,

    // 허가번호구분코드
    @JsonProperty("pmsnoGbCd")
    val pmsnoGbCd: String? = null,

    // 허가번호구분코드명
    @JsonProperty("pmsnoGbCdNm")
    val pmsnoGbCdNm: String? = null,

    // 호수(호)
    @JsonProperty("hoCnt")
    val hoCnt: Int? = null,

    // 에너지효율등급
    @JsonProperty("engrGrade")
    val engrGrade: Int? = null,

    // 에너지절감율
    @JsonProperty("engrRat")
    val engrRat: Double? = null,

    // EPI점수
    @JsonProperty("engrEpi")
    val engrEpi: Double? = null,

    // 친환경건축물등급
    @JsonProperty("gnBldGrade")
    val gnBldGrade: Int? = null,

    // 친환경건축물인증점수
    @JsonProperty("gnBldCert")
    val gnBldCert: Int? = null,

    // 지능형건축물등급
    @JsonProperty("itgBldGrade")
    val itgBldGrade: Int? = null,

    // 지능형건축물인증점수
    @JsonProperty("itgBldCert")
    val itgBldCert: Int? = null,

    // 생성일자
    @JsonProperty("crtnDay")
    @JsonDeserialize(using = StringToInstantDeserializer::class)
    val crtnDay: Instant? = null,

    // 순번
    @JsonProperty("rnum")
    val rnum: Int? = null,

    // 대지위치
    @JsonProperty("platPlc")
    val platPlc: String? = null,

    // 시군구코드
    @JsonProperty("sigunguCd")
    val sigunguCd: String? = null,

    // 법정동코드
    @JsonProperty("bjdongCd")
    val bjdongCd: String? = null,

    // 대지구분코드
    @JsonProperty("platGbCd")
    val platGbCd: String? = null,

    // 번
    @JsonProperty("bun")
    val bun: String? = null,

    // 지
    @JsonProperty("ji")
    val ji: String? = null,

    // 관리건축물대장PK
    @JsonProperty("mgmBldrgstPk")
    val mgmBldrgstPk: String? = null,

    // 대장구분코드
    @JsonProperty("regstrGbCd")
    val regstrGbCd: String? = null,

    // 대장구분코드명
    @JsonProperty("regstrGbCdNm")
    val regstrGbCdNm: String? = null,

    // 대장종류코드
    @JsonProperty("regstrKindCd")
    val regstrKindCd: String? = null,

    // 대장종류코드명
    @JsonProperty("regstrKindCdNm")
    val regstrKindCdNm: String? = null,

    // 도로명대지위치
    @JsonProperty("newPlatPlc")
    val newPlatPlc: String? = null,

    // 건물명
    @JsonProperty("bldNm")
    val bldNm: String? = null,

    // 특수지명
    @JsonProperty("splotNm")
    val splotNm: String? = null,

    // 블록
    @JsonProperty("block")
    val block: String? = null,

    // 로트
    @JsonProperty("lot")
    val lot: String? = null,

    // 외필지수
    @JsonProperty("bylotCnt")
    val bylotCnt: String? = null,

    // 새주소도로코드
    @JsonProperty("naRoadCd")
    val naRoadCd: String? = null,

    // 새주소법정동코드
    @JsonProperty("naBjdongCd")
    val naBjdongCd: String? = null,

    // 새주소지상지하코드
    @JsonProperty("naUgrndCd")
    val naUgrndCd: String? = null,

    // 새주소본번
    @JsonProperty("naMainBun")
    val naMainBun: String? = null,

    // 새주소부번
    @JsonProperty("naSubBun")
    val naSubBun: String? = null,

    // 동명칭
    @JsonProperty("dongNm")
    val dongNm: String? = null,

    // 주부속구분코드
    @JsonProperty("mainAtchGbCd")
    val mainAtchGbCd: String? = null,

    // 주부속구분코드명
    @JsonProperty("mainAtchGbCdNm")
    val mainAtchGbCdNm: String? = null,

    // 대지면적(㎡)
    @JsonProperty("platArea")
    val platArea: Double? = null,

    // 건축면적(㎡)
    @JsonProperty("archArea")
    val archArea: Double? = null,

    // 건폐율(%)
    @JsonProperty("bcRat")
    val bcRat: Double? = null,

    // 연면적(㎡)
    @JsonProperty("totArea")
    val totArea: Double? = null,

    // 용적률산정연면적(㎡)
    @JsonProperty("vlRatEstmTotArea")
    val vlRatEstmTotArea: Double? = null,

    // 용적률(%)
    @JsonProperty("vlRat")
    val vlRat: Double? = null,

    // 구조코드
    @JsonProperty("strctCd")
    val strctCd: String? = null,

    // 구조코드명
    @JsonProperty("strctCdNm")
    val strctCdNm: String? = null,

    // 기타구조
    @JsonProperty("etcStrct")
    val etcStrct: String? = null,

    // 주용도코드
    @JsonProperty("mainPurpsCd")
    val mainPurpsCd: String? = null,

    // 내진설계적용여부
    @JsonProperty("rserthqkDsgnApplyYn")
    val rserthqkDsgnApplyYn: String? = null,

    // 내진능력
    @JsonProperty("rserthqkAblty")
    val rserthqkAblty: String? = null,
)
