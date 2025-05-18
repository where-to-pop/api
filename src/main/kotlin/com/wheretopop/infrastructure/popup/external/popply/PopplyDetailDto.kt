package com.wheretopop.infrastructure.popup.external.popply

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.wheretopop.shared.domain.identifier.PopupPopplyId
import java.time.Instant

// 팝업 상세 정보를 담을 데이터 클래스
data class PopupDetail(
    val name: String,
    val address: String,
    val optionalAddress: String?,
    val startDate: Instant?,
    val endDate: Instant?,
    val description: String,
    val url: String?,
    val latitude: Double?,
    val longitude: Double?,
    val organizerName: String?,
    val organizerUrl: String?,
    val popplyId: PopupPopplyId
)

// JSON-LD 스키마를 위한 데이터 클래스
// ignoreUnknown = true 설정으로 JSON 내 모든 필드를 매핑하지 않아도 됨
@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonLdData(
    @JsonProperty("@context") val context: String? = null,
    @JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val description: String? = null,
    val url: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val image: List<String>? = null,
    val eventAttendanceMode: String? = null,
    val location: List<LocationData>? = null,
    val address: AddressData? = null,
    val organizer: OrganizerData? = null,
    val geo: GeoData? = null,
    val openingHoursSpecification: List<List<OpeningHoursData>>? = null // 2중 배열 대응
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationData(
    @JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val address: AddressData? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddressData(
    @JsonProperty("@type") val type: String? = null,
    val name: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrganizerData(
    @JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val url: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeoData(
    @JsonProperty("@type") val type: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpeningHoursData(
    @JsonProperty("@type") val type: String? = null,
    val dayOfWeek: List<String>? = null,
    val opens: String? = null,
    val closes: String? = null
)