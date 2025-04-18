package com.wheretopop.domain.area
import org.springframework.stereotype.Service
import com.wheretopop.domain.area.AreaData
import com.wheretopop.shared.model.Location

@Service
class AreaServiceImpl(
    private val areaReader: AreaReader,
    private val areaStore: AreaStore,
) : AreaService {

    private val areaInfoMapper = AreaInfoMapper()

    override fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main> {
        val areas = this.areaReader.findAreas(criteria);
        return areaInfoMapper.of(areas);
    }
    
    override fun initializeAreas(): List<AreaInfo.Main> {
        // 1. 모든 기존 권역 데이터 조회
        val existingAreas = areaReader.findAreas(AreaCriteria.SearchAreaCriteria())
        val existingAreaMap = existingAreas.associateBy { it.name }
        
        // 2. 기본 권역 데이터 생성
        val defaultAreas = AreaData.createDefaultAreas()
        
        // 3. 저장할 Area 리스트 준비
        val areasToSave = mutableListOf<Area>()
        
        // 4. 동기화 진행 (추가하거나 업데이트할 항목 식별)
        val syncedAreas = defaultAreas.map { defaultArea ->
            val existingArea = existingAreaMap[defaultArea.name]
            
            if (existingArea == null) {
                // 없는 권역은 새로 추가 목록에 추가
                areasToSave.add(defaultArea)
                defaultArea
            } else {
                // 이미 존재하는 권역은 좌표 업데이트 (필요한 경우)
                if (existingArea.location.latitude != defaultArea.location.latitude || 
                    existingArea.location.longitude != defaultArea.location.longitude) {
                    // updateLocation 메서드를 사용하여 업데이트
                    val updatedArea = existingArea.updateLocation(defaultArea.location)
                    areasToSave.add(updatedArea)
                    updatedArea
                } else {
                    existingArea
                }
            }
        }
        
        // 5. 일괄 저장 처리
        if (areasToSave.isNotEmpty()) {
            areasToSave.forEach { area ->
                areaStore.save(area)
            }
        }
        
        return areaInfoMapper.of(syncedAreas)
    }
}
