package com.wheretopop.domain.area
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import org.springframework.stereotype.Service

@Service
class AreaServiceImpl(
    private val areaReader: AreaReader,
    private val areaStore: AreaStore,
    private val areaInsightProvider: AreaInsightProvider,
) : AreaService {

    private val areaInfoMapper = AreaInfoMapper()

    override fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main> {
        val areas = this.areaReader.findAreas(criteria);
        return areaInfoMapper.of(areas);
    }
    override fun findAll(): List<AreaInfo.Main> {
        val areas = this.areaReader.findAll();
        return areaInfoMapper.of(areas);
    }

    override fun getAreaDetailById(id: AreaId): AreaInfo.Detail {
        val area = this.areaReader.findById(id) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException();
        val areaPopulationInsight = this.areaInsightProvider.findPopulationInsightByAreaId(id) ;
        return areaInfoMapper.of(area, areaPopulationInsight);
    }

    override fun searchNearest(latitude: Double, longitude: Double): AreaInfo.Main? {
        val area = this.areaReader.findByCoordinates(latitude, longitude)
        if (area == null) return null
        return areaInfoMapper.of(area)
    }

}
