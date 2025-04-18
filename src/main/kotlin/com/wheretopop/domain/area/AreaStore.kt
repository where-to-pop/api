package com.wheretopop.domain.area

import com.wheretopop.domain.area.Area

interface AreaStore {
    fun save(area: Area): Area
    fun save(areas: List<Area>): List<Area>
    fun delete(area: Area)
    fun delete(areas: List<Area>)
}