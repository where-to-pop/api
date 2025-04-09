package com.wheretopop.domain.area

import com.wheretopop.shared.model.UniqueId

/**
 * 도메인 객체에서 DTO로 변환하는 매퍼 클래스
 */
class AreaInfoMapper {

    /**
     * Area 도메인 객체를 AreaInfo.Main DTO로 변환
     */
    fun of(domain: Area): AreaInfo.Main {
        return AreaInfo.Main(
            id = domain.id.value,
            name = domain.name,
            description = domain.description,
            location = AreaInfo.LocationInfo(
                latitude = domain.location.latitude,
                longitude = domain.location.longitude
            ),
            regionId = domain.regionId,
            statistics = domain.statistics.map { of(it) }
        )
    }

    /**
     * Area 도메인 객체 목록을 AreaInfo.Main DTO 목록으로 변환
     */
    fun of(domains: List<Area>): List<AreaInfo.Main> {
        return domains.map(::of)
    }
    
    /**
     * AreaStatistic 도메인 객체를 AreaInfo.StatisticInfo DTO로 변환
     */
    private fun of(domain: AreaStatistic): AreaInfo.StatisticInfo {
        return AreaInfo.StatisticInfo(
            id = domain.id.value,
            collectedAt = domain.collectedAt,
            commercial = of(domain.commercial),
            realEstate = of(domain.realEstate),
            social = of(domain.social),
            demographic = of(domain.demographic)
        )
    }
    
    /**
     * CommercialStatistic 도메인 객체를 AreaInfo.CommercialInfo DTO로 변환
     */
    private fun of(domain: CommercialStatistic): AreaInfo.CommercialInfo {
        return AreaInfo.CommercialInfo(
            storeCount = domain.storeCount,
            newStoreCount = domain.newStoreCount,
            closedStoreCount = domain.closedStoreCount,
            popupFrequencyCount = domain.popupFrequencyCount,
            eventCount = domain.eventCount,
            mainStoreCategories = domain.mainStoreCategories,
            brandDistribution = domain.brandDistribution
        )
    }
    
    /**
     * RealEstateStatistic 도메인 객체를 AreaInfo.RealEstateInfo DTO로 변환
     */
    private fun of(domain: RealEstateStatistic): AreaInfo.RealEstateInfo {
        return AreaInfo.RealEstateInfo(
            averageRent = domain.averageRent,
            averageVacancyRate = domain.averageVacancyRate,
            minRent = domain.minRent,
            maxRent = domain.maxRent,
            recentPriceTrend = domain.recentPriceTrend,
            buildingCount = domain.buildingCount,
            averageBuildingAge = domain.averageBuildingAge
        )
    }
    
    /**
     * SocialStatistic 도메인 객체를 AreaInfo.SocialInfo DTO로 변환
     */
    private fun of(domain: SocialStatistic): AreaInfo.SocialInfo {
        return AreaInfo.SocialInfo(
            snsMentionCount = domain.snsMentionCount,
            positiveSentimentRatio = domain.positiveSentimentRatio,
            negativeSentimentRatio = domain.negativeSentimentRatio,
            neutralSentimentRatio = domain.neutralSentimentRatio,
            topKeywords = domain.topKeywords,
            topHashtags = domain.topHashtags,
            instagramPostCount = domain.instagramPostCount,
            blogPostCount = domain.blogPostCount,
            newsArticleCount = domain.newsArticleCount
        )
    }
    
    /**
     * DemographicStatistic 도메인 객체를 AreaInfo.DemographicInfo DTO로 변환
     */
    private fun of(domain: DemographicStatistic): AreaInfo.DemographicInfo {
        return AreaInfo.DemographicInfo(
            floatingPopulation = domain.floatingPopulation,
            populationDensityValue = domain.populationDensityValue,
            weekdayPeakHourPopulation = domain.weekdayPeakHourPopulation,
            weekendPeakHourPopulation = domain.weekendPeakHourPopulation,
            ageDistribution = domain.ageDistribution,
            genderRatio = domain.genderRatio,
            visitorResidenceDistribution = domain.visitorResidenceDistribution,
            transportationUsage = domain.transportationUsage
        )
    }
}