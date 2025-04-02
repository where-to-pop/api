package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.AreaInfo
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

object AreaDataModel {

    @Document(indexName = "area_index")
    data class SearchDocument(
        @Id
        val id: Long,
        val token: String,
        val name: String,
        val provinceName: String,
        val cityName: String,
        val totalFloatingPopulation: Int,
        val maleFloatingPopulation: Int,
        val femaleFloatingPopulation: Int,
        val populationDensity: Int
    )

    fun SearchDocument.toDomain(): AreaInfo.Main = AreaInfo.Main(
        id = id,
        token = token,
        name = name,
        provinceName = provinceName,
        cityName = cityName,
        totalFloatingPopulation = totalFloatingPopulation,
        maleFloatingPopulation = maleFloatingPopulation,
        femaleFloatingPopulation = femaleFloatingPopulation,
        populationDensity = populationDensity
    )

//    @jakarta.persistence.Entity
//    @jakarta.persistence.Table(name = "area")
//    data class JpaEntity(
//        @jakarta.persistence.Id
//        val id: Long,
//        val areaToken: String,
//        val provinceName: String,
//        val cityName: String,
//        val totalFloatingPopulation: Int,
//        val maleFloatingPopulation: Int,
//        val femaleFloatingPopulation: Int,
//        val populationDensity: Int
//    )

// CacheModel 등
}
