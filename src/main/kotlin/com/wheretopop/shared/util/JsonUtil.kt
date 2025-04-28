package com.wheretopop.shared.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.Gender

/**
 * JSON 데이터 변환을 위한 유틸리티 클래스
 */
object JsonUtil {
    @JvmStatic
    val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        // Java 8 시간 관련 모듈 등록
        registerModule(JavaTimeModule())
        // Instant를 타임스탬프가 아닌 ISO-8601로 직렬화
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    /**
     * 객체를 JSON 문자열로 변환
     */
    @JvmStatic
    fun <T> toJson(obj: T): String {
        return objectMapper.writeValueAsString(obj)
    }

    /**
     * JSON 문자열을 객체로 변환
     */
    @JvmStatic
    fun <T> fromJson(json: String?, clazz: Class<T>): T {
        if (json.isNullOrBlank()) {
            throw IllegalArgumentException("JSON string cannot be null or blank")
        }
        return objectMapper.readValue(json, clazz)
    }

    /**
     * JSON 문자열을 객체로 변환 (리스트)
     */
    @JvmStatic
    fun <T> fromJsonList(json: String?, clazz: Class<T>): List<T> {
        if (json.isNullOrBlank()) {
            throw IllegalArgumentException("JSON string cannot be null or blank")
        }
        val listType = objectMapper.typeFactory.constructCollectionType(List::class.java, clazz)
        return objectMapper.readValue(json, listType)
    }

    /**
     * JSON 문자열을 리스트로 변환 (null 가능)
     */
    @JvmStatic
    fun <T> fromJsonListOrNull(json: String?, clazz: Class<T>): List<T>? {
        return if (json.isNullOrBlank()) null else fromJsonList(json, clazz)
    }

    /**
     * JSON 문자열을 객체로 변환 (null 가능)
     */
    @JvmStatic
    fun <T> fromJsonOrNull(json: String?, clazz: Class<T>): T? {
        return if (json.isNullOrBlank()) null else fromJson(json, clazz)
    }
}


/**
 * 상점 카테고리 데이터 클래스
 * 각 상점 카테고리별 개수를 저장
 */
data class StoreCategory(
    val name: String,         // 카테고리 이름 (예: "카페", "의류", "식당")
    val count: Int,           // 해당 카테고리 매장 수
    val percentage: Double? = null  // 전체 매장 중 비율 (%)
)

/**
 * 브랜드 분포 데이터 클래스
 * 지역 내 브랜드 분포 정보를 저장
 */
data class BrandDistribution(
    val brandName: String,    // 브랜드 이름
    val count: Int,           // 해당 브랜드 매장 수
    val category: String? = null,    // 브랜드 카테고리 (예: "패션", "식품")
    val percentage: Double? = null  // 전체 브랜드 중 비율 (%)
)

/**
 * 키워드 정보 데이터 클래스
 * 소셜 미디어 등에서 언급된 키워드 정보를 저장
 */
data class Keyword(
    val word: String,         // 키워드 단어
    val count: Int,           // 언급 횟수
    val sentiment: String? = null,  // 감성 분석 결과 (예: "positive", "negative", "neutral")
    val relatedWords: List<String>? = null  // 연관 키워드
)

/**
 * 해시태그 정보 데이터 클래스
 * 소셜 미디어에서 사용된 해시태그 정보를 저장
 */
data class Hashtag(
    val tag: String,          // 해시태그 (예: "#팝업스토어")
    val count: Int,           // 사용 횟수
    val category: String? = null,    // 해시태그 카테고리 (예: "브랜드", "장소")
    val trending: Boolean? = null    // 트렌딩 여부
)

/**
 * 연령대별 분포 데이터 클래스
 * 방문자의 연령대별 분포 정보를 저장
 */
data class AgeDistribution(
    val ageGroup: AgeGroup,   // 연령대 (열거형: TEEN_AND_UNDER, TWENTIES 등)
    val count: Int,           // 해당 연령대 방문자 수
    val percentage: Double    // 전체 방문자 중 비율 (%)
)

/**
 * 성별 비율 데이터 클래스
 * 방문자의 성별 분포 정보를 저장
 */
data class GenderRatio(
    val gender: Gender,       // 성별 (열거형: MALE, FEMALE)
    val count: Int,           // 해당 성별 방문자 수
    val percentage: Double    // 전체 방문자 중 비율 (%)
)

/**
 * 방문자 거주지 분포 데이터 클래스
 * 방문자의 거주 지역별 분포 정보를 저장
 */
data class VisitorResidence(
    val region: String,       // 지역명 (예: "서울", "경기")
    val count: Int,           // 해당 지역 방문자 수
    val percentage: Double,   // 전체 방문자 중 비율 (%)
    val district: String? = null      // 세부 지역 (예: "강남구", "마포구")
)

/**
 * 교통수단 이용 비율 데이터 클래스
 * 방문자가 이용한 교통수단별 비율 정보를 저장
 */
data class TransportationUsage(
    val transportType: String,  // 교통수단 (예: "지하철", "버스", "자가용")
    val count: Int,             // 해당 교통수단 이용자 수
    val percentage: Double,     // 전체 방문자 중 비율 (%)
    val averageTime: Int? = null  // 평균 이동 시간 (분)
) 