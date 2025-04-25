package com.wheretopop.infrastructure.area.bootstrap

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.model.Location
import java.time.Instant

/**
 * 주요 권역 정보 데이터
 * 권역명과 해당 위도/경도 좌표 매핑
 */
object AreaSeedData {
    // 권역명과 [위도, 경도] 매핑 데이터
    private val areaCoordinates = mapOf(
        "강남 MICE 관광특구" to doubleArrayOf(37.5117, 127.0592),
        "동대문 관광특구" to doubleArrayOf(37.5665, 127.0092),
        "명동 관광특구" to doubleArrayOf(37.5638, 126.9845),
        "이태원 관광특구" to doubleArrayOf(37.5345, 126.9946),
        "잠실 관광특구" to doubleArrayOf(37.5125, 127.1025),
        "홍대 관광특구" to doubleArrayOf(37.5532600788241, 126.921835828416),
        "강남역" to doubleArrayOf(37.497983, 127.027788),
        "건대입구역" to doubleArrayOf(37.5404, 127.0693),
        "고속터미널역" to doubleArrayOf(37.5049, 127.0049),
        "사당역" to doubleArrayOf(37.4766, 126.9816),
        "서울역" to doubleArrayOf(37.5559, 126.9723),
        "선릉역" to doubleArrayOf(37.5045, 127.049),
        "신촌·이대역" to doubleArrayOf(37.55773, 126.9399),
        "충정로역" to doubleArrayOf(37.5577327, 126.9398711),
        "합정역" to doubleArrayOf(37.5496, 126.914),
        "혜화역" to doubleArrayOf(37.5822, 127.0019),
        "가로수길" to doubleArrayOf(37.5211, 127.0229),
        "북촌한옥마을" to doubleArrayOf(37.5815, 126.985),
        "서촌" to doubleArrayOf(37.5787, 126.9731),
        "성수카페거리" to doubleArrayOf(37.5446, 127.058),
        "압구정로데오거리" to doubleArrayOf(37.5268, 127.0389),
        "여의도" to doubleArrayOf(37.52584, 126.9249),
        "연남동" to doubleArrayOf(37.5627, 126.9214),
        "영등포 타임스퀘어" to doubleArrayOf(37.5171, 126.9033),
        "용리단길" to doubleArrayOf(37.5314, 126.9722),
        "인사동·익선동" to doubleArrayOf(37.57404, 126.9873),
        "해방촌·경리단길" to doubleArrayOf(37.54237, 126.9878),
        "광화문광장" to doubleArrayOf(37.5724, 126.9769)
    )

    /**
     * 기본 Area 도메인 객체를 생성합니다.
     * @return 권역 도메인 객체 리스트
     */
    fun createDefaultAreas(): List<Area> {
        return areaCoordinates.map { (name, coordinates) ->
            Area.create(
                name = name,
                location = Location(coordinates[0], coordinates[1]),
                description = "${name}의 주요 상권 및 관광지 정보",
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null
            )
        }
    }
} 