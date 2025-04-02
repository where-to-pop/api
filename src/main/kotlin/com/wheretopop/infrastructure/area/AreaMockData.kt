package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.Location
import com.wheretopop.domain.area.PopulationIndicators


object AreaMockData {

    val areaList = listOf(
        Area.create(id = 1, token = "area-001", name = "종로1가", location = Location(province = "서울특별시", city = "종로구"), indicators = PopulationIndicators(totalFloatingPopulation = 10000, maleFloatingPopulation = 6000, femaleFloatingPopulation = 4000, populationDensity = 4500)),
        Area.create(id = 2, token = "area-002", name = "종로2가", location = Location(province = "서울특별시", city = "종로구"), indicators = PopulationIndicators(totalFloatingPopulation = 8500, maleFloatingPopulation = 4700, femaleFloatingPopulation = 3800, populationDensity = 4000)),
        Area.create(id = 3, token = "area-003", name = "종로3가", location = Location(province = "서울특별시", city = "종로구"), indicators = PopulationIndicators(totalFloatingPopulation = 9500, maleFloatingPopulation = 5200, femaleFloatingPopulation = 4300, populationDensity = 4200)),
        Area.create(id = 4, token = "area-004", name = "종로4가", location = Location(province = "서울특별시", city = "종로구"), indicators = PopulationIndicators(totalFloatingPopulation = 7000, maleFloatingPopulation = 3900, femaleFloatingPopulation = 3100, populationDensity = 3700)),
        Area.create(id = 5, token = "area-005", name = "견지동", location = Location(province = "서울특별시", city = "종로구"), indicators = PopulationIndicators(totalFloatingPopulation = 6000, maleFloatingPopulation = 3300, femaleFloatingPopulation = 2700, populationDensity = 3500)),

        Area.create(id = 6, token = "area-006", name = "중앙동", location = Location(province = "부산광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 9000, maleFloatingPopulation = 5000, femaleFloatingPopulation = 4000, populationDensity = 4100)),
        Area.create(id = 7, token = "area-007", name = "동광동", location = Location(province = "부산광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 7500, maleFloatingPopulation = 4100, femaleFloatingPopulation = 3400, populationDensity = 3900)),
        Area.create(id = 8, token = "area-008", name = "대청동", location = Location(province = "부산광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 6800, maleFloatingPopulation = 3700, femaleFloatingPopulation = 3100, populationDensity = 3600)),
        Area.create(id = 9, token = "area-009", name = "보수동", location = Location(province = "부산광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 7200, maleFloatingPopulation = 3900, femaleFloatingPopulation = 3300, populationDensity = 3700)),
        Area.create(id = 10, token = "area-010", name = "부평동", location = Location(province = "부산광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 5600, maleFloatingPopulation = 3000, femaleFloatingPopulation = 2600, populationDensity = 3400)),

        Area.create(id = 11, token = "area-011", name = "수내동", location = Location(province = "경기도", city = "성남시 분당구"), indicators = PopulationIndicators(totalFloatingPopulation = 8000, maleFloatingPopulation = 4300, femaleFloatingPopulation = 3700, populationDensity = 3800)),
        Area.create(id = 12, token = "area-012", name = "정자동", location = Location(province = "경기도", city = "성남시 분당구"), indicators = PopulationIndicators(totalFloatingPopulation = 8200, maleFloatingPopulation = 4400, femaleFloatingPopulation = 3800, populationDensity = 3900)),
        Area.create(id = 13, token = "area-013", name = "구미동", location = Location(province = "경기도", city = "성남시 분당구"), indicators = PopulationIndicators(totalFloatingPopulation = 7800, maleFloatingPopulation = 4200, femaleFloatingPopulation = 3600, populationDensity = 3700)),
        Area.create(id = 14, token = "area-014", name = "금곡동", location = Location(province = "경기도", city = "성남시 분당구"), indicators = PopulationIndicators(totalFloatingPopulation = 7000, maleFloatingPopulation = 3800, femaleFloatingPopulation = 3200, populationDensity = 3500)),
        Area.create(id = 15, token = "area-015", name = "판교동", location = Location(province = "경기도", city = "성남시 분당구"), indicators = PopulationIndicators(totalFloatingPopulation = 7300, maleFloatingPopulation = 4000, femaleFloatingPopulation = 3300, populationDensity = 3600)),

        Area.create(id = 16, token = "area-016", name = "동인동", location = Location(province = "대구광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 6400, maleFloatingPopulation = 3400, femaleFloatingPopulation = 3000, populationDensity = 3400)),
        Area.create(id = 17, token = "area-017", name = "삼덕동", location = Location(province = "대구광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 5200, maleFloatingPopulation = 2700, femaleFloatingPopulation = 2500, populationDensity = 3300)),
        Area.create(id = 18, token = "area-018", name = "금남로", location = Location(province = "광주광역시", city = "동구"), indicators = PopulationIndicators(totalFloatingPopulation = 5900, maleFloatingPopulation = 3100, femaleFloatingPopulation = 2800, populationDensity = 3500)),
        Area.create(id = 19, token = "area-019", name = "신포동", location = Location(province = "인천광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 6700, maleFloatingPopulation = 3600, femaleFloatingPopulation = 3100, populationDensity = 3700)),
        Area.create(id = 20, token = "area-020", name = "은행동", location = Location(province = "대전광역시", city = "중구"), indicators = PopulationIndicators(totalFloatingPopulation = 6100, maleFloatingPopulation = 3200, femaleFloatingPopulation = 2900, populationDensity = 3400)),
    )
}
