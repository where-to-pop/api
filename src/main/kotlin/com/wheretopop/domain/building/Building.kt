package com.wheretopop.domain.building

import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.model.Location
import java.time.LocalDateTime

/**
 * Building 도메인 모델
 * 건물 정보를 나타내는 애그리거트 루트 엔티티
 */
class Building private constructor(
    val id: UniqueId,
    val name: String,
    val address: String,
    val areaId: Long?,
    val regionId: Long?,
    val location: Location?,
    val totalFloorArea: Double?,
    val hasElevator: Boolean?,
    val parkingInfo: String?,
    val buildingSize: Double?
) {
    // 건물 통계 정보 목록
    private val _statistics: MutableList<BuildingStatistic> = mutableListOf()
    val statistics: List<BuildingStatistic> get() = _statistics.toList()

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            name: String,
            address: String,
            areaId: Long? = null,
            regionId: Long? = null,
            location: Location? = null,
            totalFloorArea: Double? = null,
            hasElevator: Boolean? = null,
            parkingInfo: String? = null,
            buildingSize: Double? = null
        ): Building {
            require(name.isNotBlank()) { "건물 이름은 비어있을 수 없습니다." }
            
            return Building(
                id = id,
                name = name,
                address = address,
                areaId = areaId,
                regionId = regionId,
                location = location,
                totalFloorArea = totalFloorArea,
                hasElevator = hasElevator,
                parkingInfo = parkingInfo,
                buildingSize = buildingSize
            )
        }
    }
    
    /**
     * 통계 정보 추가
     */
    fun addStatistic(statistic: BuildingStatistic) {
        _statistics.add(statistic)
    }
    
    /**
     * 최신 통계 정보 조회
     */
    fun getLatestStatistic(): BuildingStatistic? {
        return statistics.maxByOrNull { it.collectedAt }
    }
    
    /**
     * 특정 기간 내 통계 정보 조회
     */
    fun getStatisticsBetween(from: LocalDateTime, to: LocalDateTime): List<BuildingStatistic> {
        return statistics.filter { 
            it.collectedAt.isAfter(from) && it.collectedAt.isBefore(to) 
        }
    }
    
    /**
     * 건물 이름에서 주요 키워드 추출
     */
    fun extractNameKeywords(): List<String> {
        return name.split(" ", "-", "_")
            .filter { it.length >= 2 }
            .map { it.lowercase() }
    }
    
    /**
     * 주소에서 지역 추출
     */
    fun extractRegionFromAddress(): String? {
        return address?.split(" ")?.firstOrNull()
    }
} 