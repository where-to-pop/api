package com.wheretopop.domain.area
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

    override suspend fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main> {
        val areas = this.areaReader.findAreas(criteria);
        return areaInfoMapper.of(areas);
    }
    override suspend fun findAll(): List<AreaInfo.Main> {
        val areas = this.areaReader.findAll();
        return areaInfoMapper.of(areas);
    }

    override suspend fun getAreaDetailById(id: AreaId): AreaInfo.Detail {
        val area = this.areaReader.findById(id) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException();
        val areaPopulationInsight = this.areaInsightProvider.findPopulationInsightByAreaId(id) ;
        return areaInfoMapper.of(area, areaPopulationInsight);
    }

}
