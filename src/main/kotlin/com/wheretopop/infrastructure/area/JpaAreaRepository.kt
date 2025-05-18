package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.shared.infrastructure.entity.AreaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * JPA 지역 저장소 인터페이스
 */
@Repository
interface JpaAreaRepository : JpaRepository<AreaEntity, Long> {
    @Query("SELECT a FROM AreaEntity a WHERE a.name = :name AND a.deletedAt IS NULL")
    fun findByName(@Param("name") name: String): AreaEntity?
    
    @Query("SELECT a FROM AreaEntity a WHERE a.deletedAt IS NULL")
    override fun findAll(): List<AreaEntity>

    /**
     * 주어진 위도/경도에서 가장 가까운 지역을 찾습니다.
     * Haversine 공식을 사용하여 두 지점 간의 실제 거리를 계산합니다.
     * R = 지구 반경 (6371km)
     * distance = 2 * R * asin(sqrt(sin²((lat2-lat1)/2) + cos(lat1)cos(lat2)sin²((lon2-lon1)/2)))
     */
    @Query("""
        SELECT a FROM AreaEntity a 
        WHERE a.deletedAt IS NULL 
        ORDER BY (6371 * 
            acos(cos(radians(:latitude)) * 
                cos(radians(a.latitude)) * 
                cos(radians(a.longitude) - radians(:longitude)) + 
                sin(radians(:latitude)) * 
                sin(radians(a.latitude))
            )
        ) ASC
    """)
    fun findNearest(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("maxDistanceKm") maxDistanceKm: Double
    ): List<AreaEntity>
}

/**
 * 지역 저장소 JPA 구현체
 */
@Repository
class AreaRepositoryJpaAdapter(
    private val jpaRepository: JpaAreaRepository
) : AreaRepository {

    override fun findById(id: AreaId): Area? =
        jpaRepository.findById(id.toLong()).orElse(null)?.toDomain()

    override fun findByName(name: String): Area? =
        jpaRepository.findByName(name)?.toDomain()

    override fun findAll(): List<Area> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun save(area: Area): Area {
        val entity = AreaEntity.from(area)
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun save(areas: List<Area>): List<Area> =
        areas.map { save(it) }

    override fun deleteById(id: AreaId) {
        jpaRepository.findById(id.toLong()).ifPresent { entity ->
            entity.deletedAt = Instant.now()
            jpaRepository.save(entity)
        }
    }

    /**
     * 주어진 위도/경도에서 가장 가까운 지역을 찾습니다.
     */
    override fun findNearest(latitude: Double, longitude: Double, maxDistanceKm: Double): Area? {
        val nearestAreas = jpaRepository.findNearest(latitude, longitude, maxDistanceKm)
        
        // 최대 거리 내의 첫 번째 지역만 반환
        return nearestAreas
            .firstOrNull { area ->
                calculateDistance(
                    latitude1 = latitude,
                    longitude1 = longitude,
                    latitude2 = area.latitude,
                    longitude2 = area.longitude
                ) <= maxDistanceKm
            }?.toDomain()
    }

    /**
     * Haversine 공식을 사용하여 두 지점 간의 거리를 계산합니다 (킬로미터 단위).
     */
    private fun calculateDistance(
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): Double {
        val earthRadius = 6371.0 // 지구 반경 (km)
        
        val lat1 = Math.toRadians(latitude1)
        val lon1 = Math.toRadians(longitude1)
        val lat2 = Math.toRadians(latitude2)
        val lon2 = Math.toRadians(longitude2)
        
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        
        val a = Math.sin(dLat / 2).pow(2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon / 2).pow(2)
        
        val c = 2 * Math.asin(Math.sqrt(a))
        
        return earthRadius * c
    }
}

