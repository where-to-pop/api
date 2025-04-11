package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.*
import com.wheretopop.infrastructure.area.*
import com.wheretopop.shared.model.statistics.*
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.model.Location
import java.time.LocalDateTime
import java.util.UUID

/**
 * 인프라 계층 엔티티와 도메인 모델 간 변환을 담당하는 매퍼
 */
class AreaMapper {

    /**
     * AreaEntity를 Area 도메인 모델로 변환
     */
    fun toDomain(entity: AreaEntity): Area {
        val location = entity.latitude?.let { lat ->
            entity.longitude?.let { lng ->
                Location.of(lat, lng)
            }
        } ?: Location.of(0.0, 0.0)

        val area = Area.create(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            location = location,
            regionId = entity.regionId
        )

        // 통계 정보 변환 및 추가
        entity.statistics.forEach { statisticEntity ->
            val statistic = toAreaStatistic(statisticEntity, entity.id)
            area.addStatistic(statistic)
        }

        return area
    }

    /**
     * Area 도메인 모델을 AreaEntity로 변환
     */
    fun toEntity(domain: Area): AreaEntity {
        val entity = AreaEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            latitude = domain.location.latitude,
            longitude = domain.location.longitude,
            regionId = domain.regionId
        )

        // 도메인 모델의 통계 정보를 엔티티로 변환
        val statisticEntities = domain.statistics.map { toAreaStatisticEntity(it, entity) }
        statisticEntities.forEach { entity.addStatistic(it) }

        return entity
    }

    /**
     * AreaStatisticEntity를 AreaStatistic 도메인 모델로 변환
     */
    fun toAreaStatistic(entity: AreaStatisticEntity, areaId: UniqueId): AreaStatistic {
        return AreaStatistic.create(
            id = entity.id,
            areaId = areaId,
            collectedAt = entity.collectedAt,
            commercial = toCommercialStatistic(entity.commercialInfo),
            realEstate = toRealEstateStatistic(entity.realEstateInfo),
            social = toSocialStatistic(entity.socialInfo),
            demographic = toDemographicStatistic(entity.demographicInfo)
        )
    }

    /**
     * AreaStatistic 도메인 모델을 AreaStatisticEntity로 변환
     */
    fun toAreaStatisticEntity(domain: AreaStatistic, areaEntity: AreaEntity): AreaStatisticEntity {
        val entity = AreaStatisticEntity(
            id = domain.id,
            collectedAt = domain.collectedAt,
            commercialInfo = toCommercialInfo(domain.commercial),
            realEstateInfo = toRealEstateInfo(domain.realEstate),
            socialInfo = toSocialInfo(domain.social),
            demographicInfo = toDemographicInfo(domain.demographic)
        )
        entity.area = areaEntity
        return entity
    }

    /**
     * CommercialInfo 변환
     */
    private fun toCommercialStatistic(entity: CommercialInfo): CommercialStatistic {
        return CommercialStatistic(
            storeCount = entity.storeCount,
            newStoreCount = entity.newStoreCount,
            closedStoreCount = entity.closedStoreCount,
            popupFrequencyCount = entity.popupFrequencyCount,
            eventCount = entity.eventCount,
            mainStoreCategories = entity.mainStoreCategories,
            brandDistribution = entity.brandDistribution
        )
    }

    private fun toCommercialInfo(domain: CommercialStatistic): CommercialInfo {
        return CommercialInfo(
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
     * RealEstateInfo 변환
     */
    private fun toRealEstateStatistic(entity: RealEstateInfo): RealEstateStatistic {
        return RealEstateStatistic(
            averageRent = entity.averageRent,
            averageVacancyRate = entity.averageVacancyRate,
            minRent = entity.minRent,
            maxRent = entity.maxRent,
            recentPriceTrend = entity.recentPriceTrend,
            buildingCount = entity.buildingCount,
            averageBuildingAge = entity.averageBuildingAge
        )
    }

    private fun toRealEstateInfo(domain: RealEstateStatistic): RealEstateInfo {
        return RealEstateInfo(
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
     * SocialInfo 변환
     */
    private fun toSocialStatistic(entity: SocialInfo): SocialStatistic {
        return SocialStatistic(
            snsMentionCount = entity.snsMentionCount,
            positiveSentimentRatio = entity.positiveSentimentRatio,
            negativeSentimentRatio = entity.negativeSentimentRatio,
            neutralSentimentRatio = entity.neutralSentimentRatio,
            topKeywords = entity.topKeywords,
            topHashtags = entity.topHashtags,
            instagramPostCount = entity.instagramPostCount,
            blogPostCount = entity.blogPostCount,
            newsArticleCount = entity.newsArticleCount
        )
    }

    private fun toSocialInfo(domain: SocialStatistic): SocialInfo {
        return SocialInfo(
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
     * DemographicInfo 변환
     */
    private fun toDemographicStatistic(entity: DemographicInfo): DemographicStatistic {
        return DemographicStatistic(
            floatingPopulation = entity.floatingPopulation,
            populationDensityValue = entity.populationDensityValue,
            weekdayPeakHourPopulation = entity.weekdayPeakHourPopulation,
            weekendPeakHourPopulation = entity.weekendPeakHourPopulation,
            ageDistribution = entity.ageDistribution,
            genderRatio = entity.genderRatio,
            visitorResidenceDistribution = entity.visitorResidenceDistribution,
            transportationUsage = entity.transportationUsage
        )
    }

    private fun toDemographicInfo(domain: DemographicStatistic): DemographicInfo {
        return DemographicInfo(
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