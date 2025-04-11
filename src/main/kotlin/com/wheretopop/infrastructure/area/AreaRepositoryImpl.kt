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
     * 검색 조건을 통한 Area 조회 (SQL)
     */
    override fun findByCondition(criteria: AreaCriteria): List<Area> {
        return when (criteria) {
            is AreaCriteria.SearchAreaCriteria -> {
                // 기본 검색 조건에 따른 DB 쿼리
                val result = if (criteria.keyword != null) {
                    jpaAreaRepository.findByNameContainingIgnoreCase(criteria.keyword)
                } else if (criteria.regionId != null) {
                    jpaAreaRepository.findByRegionId(criteria.regionId)
                } else if (criteria.latitude != null && criteria.longitude != null && criteria.radius != null) {
                    jpaAreaRepository.findByLocationWithin(
                        criteria.latitude, 
                        criteria.longitude, 
                        criteria.radius
                    )
                } else {
                    jpaAreaRepository.findAll()
                }

                paginateResult(result.map { mapper.toDomain(it) }, criteria.offset, criteria.limit)
            }
            is AreaCriteria.DemographicCriteria -> {
                // 먼저 기본 검색 조건으로 조회
                val baseAreas = findByCondition(criteria.baseSearchCriteria)
                // 인구 통계 조건은 JPA로 구현이 복잡하므로 메모리에서 필터링
                filterAreasByDemographicCriteria(baseAreas, criteria)
            }
            is AreaCriteria.CommercialCriteria -> {
                // 먼저 기본 검색 조건으로 조회
                val baseAreas = findByCondition(criteria.baseSearchCriteria)
                // 상업 정보 조건은 JPA로 구현이 복잡하므로 메모리에서 필터링
                filterAreasByCommercialCriteria(baseAreas, criteria)
            }
        }
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
     * 비즈니스 타입으로 Area 조회 (SQL)
     */
    override fun findByBusinessType(businessType: String): List<Area> {
        return jpaAreaRepository.findByMainBusinessTypesContaining(businessType)
            .map { mapper.toDomain(it) }
    }
    
    /**
     * 검색 조건을 통한 Area 검색 (ES)
     */
    override fun search(criteria: AreaCriteria): List<Area> {
        return elasticsearchAreaRepository.search(criteria)
    }
    
    /**
     * 키워드 기반 Area 검색 (ES)
     */
    override fun searchByKeyword(keyword: String): List<Area> {
        return elasticsearchAreaRepository.searchByKeyword(keyword)
    }
    
    /**
     * 인구통계 기반 Area 검색 (ES)
     */
    override fun searchByDemographic(criteria: AreaCriteria.DemographicCriteria): List<Area> {
        return elasticsearchAreaRepository.searchByDemographic(criteria)
    }
    
    /**
     * 상업 정보 기반 Area 검색 (ES)
     */
    override fun searchByCommercial(criteria: AreaCriteria.CommercialCriteria): List<Area> {
        return elasticsearchAreaRepository.searchByCommercial(criteria)
    }

    /**
     * Area 저장 (SQL + ES)
     */
    override fun save(area: Area): Area {
        // JPA 저장소에 저장
        val savedEntity = jpaAreaRepository.findById(area.id)
            .orElseGet { 
                createEntityFromDomain(area)
            }
        
        updateEntityFromDomain(savedEntity, area)
        val saved = jpaAreaRepository.save(savedEntity)
        
        // Elasticsearch에도 저장
        elasticsearchAreaRepository.save(mapper.toDomain(saved))
        
        return mapper.toDomain(saved)   
    }

    /**
     * ID로 Area 삭제 (SQL + ES)
     */
    override fun deleteById(id: UniqueId) {
        jpaAreaRepository.deleteById(id)
        elasticsearchAreaRepository.deleteById(id)
    }

    // 도메인 객체에서 엔티티 생성
    private fun createEntityFromDomain(domain: Area): com.wheretopop.infrastructure.area.AreaEntity {
        return com.wheretopop.infrastructure.area.AreaEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            latitude = domain.location.latitude,
            longitude = domain.location.longitude,
            regionId = domain.regionId
        )
    }
    
    // 도메인 객체에서 엔티티 업데이트
    private fun updateEntityFromDomain(entity: com.wheretopop.infrastructure.area.AreaEntity, domain: Area) {
        entity.name = domain.name
        entity.description = domain.description
        entity.latitude = domain.location.latitude
        entity.longitude = domain.location.longitude
        entity.regionId = domain.regionId
    }
    
    // 페이징 처리 헬퍼 메서드
    private fun <T> paginateResult(list: List<T>, offset: Int, limit: Int): List<T> {
        val start = offset.coerceAtMost(list.size)
        val end = (offset + limit).coerceAtMost(list.size)
        return list.subList(start, end)
    }
    
    // 인구 통계 검색 조건으로 필터링
    private fun filterAreasByDemographicCriteria(areas: List<Area>, criteria: AreaCriteria.DemographicCriteria): List<Area> {
        return areas.filter { area ->
            val latestStat = area.getLatestStatistic()
            
            val populationMatch = criteria.minPopulation?.let { 
                latestStat?.demographic?.floatingPopulation?.let { population -> 
                    population >= it 
                }
            } ?: true
            
            val ageRangeMatch = criteria.ageRange?.let { 
                area.isPopularForAgeGroup(it, 20.0) // 20% 이상이면 해당 연령대가 주요 방문 연령대로 간주
            } ?: true

            populationMatch && ageRangeMatch
        }
    }
    
    // 상업 정보 검색 조건으로 필터링
    private fun filterAreasByCommercialCriteria(areas: List<Area>, criteria: AreaCriteria.CommercialCriteria): List<Area> {
        return areas.filter { area ->
            val latestStat = area.getLatestStatistic()
            
            val storeCountMatch = criteria.minStoreCount?.let { 
                latestStat?.commercial?.storeCount?.let { count -> 
                    count >= it 
                }
            } ?: true
            
            val categoryMatch = criteria.storeCategory?.let { category ->
                latestStat?.getCategoryPercentage(category)?.let { percentage ->
                    percentage > 0
                }
            } ?: true
            
            val rentMatch = criteria.maxRent?.let { 
                latestStat?.realEstate?.averageRent?.let { rent ->
                    rent <= it
                }
            } ?: true

            storeCountMatch && categoryMatch && rentMatch
        }
    }
} 