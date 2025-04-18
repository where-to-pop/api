package com.wheretopop.infrastructure.area

import com.wheretopop.shared.model.UniqueId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * AreaEntity를 위한 Spring Data JPA Repository 인터페이스
 */
@Repository
interface JpaAreaRepository : JpaRepository<AreaEntity, UniqueId> {
    /**
     * 이름으로 AreaEntity 조회
     */
    fun findByName(name: String): Optional<AreaEntity>
    
    /**
     * 키워드로 이름 포함 검색
     */
    fun findByNameContainingIgnoreCase(keyword: String): List<AreaEntity>
    
    /**
     * 지역 ID로 Area 목록 조회
     */
    fun findByRegionId(regionId: Long): List<AreaEntity>
    
    /**
     * 위치 기반 반경 내 Area 조회
     * Haversine 공식을 사용하여 위도/경도 기반 거리 계산
     */
    @Query("""
        SELECT a FROM AreaEntity a
        WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * 
        cos(radians(a.longitude) - radians(:longitude)) + 
        sin(radians(:latitude)) * sin(radians(a.latitude)))) < :radiusKm
    """)
    fun findByLocationWithin(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("radiusKm") radiusKm: Double
    ): List<AreaEntity>

} 