package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaCriteria
import com.wheretopop.shared.model.UniqueId
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.util.*

/**
 * AreaRepository 인터페이스의 구현체
 * JPA와 Elasticsearch 저장소를 모두 사용함
 */
@Repository
@Primary
class AreaRepositoryImpl(
    private val jpaAreaRepository: JpaAreaRepository,
    private val elasticsearchAreaRepository: ElasticsearchAreaRepository,
) : AreaRepository {
    private val mapper = AreaMapper()

    /**
     * ID로 Area 조회 (SQL)
     */
    override fun findById(id: UniqueId): Optional<Area> {
        return jpaAreaRepository.findById(id)
            .map { mapper.toDomain(it) }
    }

    /**
     * 이름으로 Area 조회 (SQL)
     */
    override fun findByName(name: String): Optional<Area> {
        return jpaAreaRepository.findByName(name)
            .map { mapper.toDomain(it) }
    }

    /**
     * 지역 ID로 Area 목록 조회 (SQL)
     */
    override fun findByRegionId(regionId: Long): List<Area> {
        return jpaAreaRepository.findByRegionId(regionId)
            .map { mapper.toDomain(it) }
    }
    
    /**
     * 위치 기반 Area 조회 (SQL)
     */
    override fun findByLocation(latitude: Double, longitude: Double, radiusKm: Double): List<Area> {
        return jpaAreaRepository.findByLocationWithin(latitude, longitude, radiusKm)
            .map { mapper.toDomain(it) }
    }

    

    /**
     * Area 저장 (SQL + ES)
     */
    override fun save(area: Area): Area {
        // JPA 저장소에 저장
        val savedEntity = jpaAreaRepository.findById(area.id)
            .orElseGet { 
                mapper.toEntity(area)
            }
        jpaAreaRepository.save(savedEntity)
        
        // Elasticsearch에도 저장
//        elasticsearchAreaRepository.save(mapper.toDomain(saved))
        
        return area;
    }

    /**
     * ID로 Area 삭제 (SQL + ES)
     */
    override fun deleteById(id: UniqueId) {
        jpaAreaRepository.deleteById(id)
        elasticsearchAreaRepository.deleteById(id)
    }

} 