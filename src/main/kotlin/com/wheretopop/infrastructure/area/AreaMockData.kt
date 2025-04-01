package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.AreaInfo


object AreaMockData {

    val areaList = listOf(
        AreaInfo.Main(1, "area-001", "서울특별시", "강남구", 10000, 6000, 4000, 4500),
        AreaInfo.Main(2, "area-002", "서울특별시", "서초구", 8500, 4700, 3800, 4000),
        AreaInfo.Main(3, "area-003", "서울특별시", "송파구", 9500, 5200, 4300, 4200),
        AreaInfo.Main(4, "area-004", "서울특별시", "마포구", 7000, 3900, 3100, 3700),
        AreaInfo.Main(5, "area-005", "서울특별시", "종로구", 6000, 3300, 2700, 3500),

        AreaInfo.Main(6, "area-006", "부산광역시", "해운대구", 9000, 5000, 4000, 4100),
        AreaInfo.Main(7, "area-007", "부산광역시", "수영구", 7500, 4100, 3400, 3900),
        AreaInfo.Main(8, "area-008", "부산광역시", "남구", 6800, 3700, 3100, 3600),
        AreaInfo.Main(9, "area-009", "부산광역시", "동래구", 7200, 3900, 3300, 3700),
        AreaInfo.Main(10, "area-010", "부산광역시", "중구", 5600, 3000, 2600, 3400),

        AreaInfo.Main(11, "area-011", "경기도", "성남시", 8000, 4300, 3700, 3800),
        AreaInfo.Main(12, "area-012", "경기도", "수원시", 8200, 4400, 3800, 3900),
        AreaInfo.Main(13, "area-013", "경기도", "고양시", 7800, 4200, 3600, 3700),
        AreaInfo.Main(14, "area-014", "경기도", "용인시", 7000, 3800, 3200, 3500),
        AreaInfo.Main(15, "area-015", "경기도", "부천시", 7300, 4000, 3300, 3600),

        AreaInfo.Main(16, "area-016", "대구광역시", "달서구", 6400, 3400, 3000, 3400),
        AreaInfo.Main(17, "area-017", "대구광역시", "중구", 5200, 2700, 2500, 3300),
        AreaInfo.Main(18, "area-018", "광주광역시", "북구", 5900, 3100, 2800, 3500),
        AreaInfo.Main(19, "area-019", "인천광역시", "연수구", 6700, 3600, 3100, 3700),
        AreaInfo.Main(20, "area-020", "대전광역시", "서구", 6100, 3200, 2900, 3400)
    )
}
