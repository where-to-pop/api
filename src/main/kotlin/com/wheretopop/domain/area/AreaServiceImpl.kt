package com.wheretopop.domain.area
import org.springframework.stereotype.Service

@Service
class AreaServiceImpl(
    private val areaReader: AreaReader,
) : AreaService {

    private val areaInfoMapper = AreaInfoMapper()

    override fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main> {
        val areas = this.areaReader.findAreas(criteria);
        return areaInfoMapper.of(areas);
    }
}
