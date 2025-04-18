package com.wheretopop.domain.area

interface AreaService {
    fun searchAreas(criteria: AreaCriteria.SearchAreaCriteria): List<AreaInfo.Main>
    

    /**
     * 기본 권역 데이터를 초기화/동기화합니다.
     * 이미 존재하는 데이터는 좌표 값을 업데이트하고, 없는 데이터는 새로 추가합니다.
     * @return 동기화된 권역 목록
     */
    fun initializeAreas(): List<AreaInfo.Main>
}