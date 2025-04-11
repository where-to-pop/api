package com.wheretopop.infrastructure.building

import com.wheretopop.domain.building.*
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.model.Location
import java.time.LocalDateTime

/**
 * 인프라 계층 엔티티와 도메인 모델 간 변환을 담당하는 매퍼
 */
class BuildingMapper {

    /**
     * BuildingEntity를 Building 도메인 모델로 변환
     */
    fun toDomain(entity: BuildingEntity): Building {
        val location = entity.latitude?.let { lat ->
            entity.longitude?.let { lng ->
                Location.of(lat, lng)
            }
        }

        val building = Building.create(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            areaId = entity.areaId,
            regionId = entity.regionId,
            location = location,
            totalFloorArea = entity.totalFloorArea,
            hasElevator = entity.hasElevator,
            parkingInfo = entity.parkingInfo,
            buildingSize = entity.buildingSize
        )

        // 통계 정보 변환 및 추가
        entity.statistics.forEach { statisticEntity ->
            val statistic = toBuildingStatistic(statisticEntity, entity.id)
            building.addStatistic(statistic)
        }

        return building
    }

    /**
     * Building 도메인 모델을 BuildingEntity로 변환
     */
    fun toEntity(domain: Building): BuildingEntity {
        val entity = BuildingEntity(
            id = domain.id,
            name = domain.name,
            address = domain.address,
            latitude = domain.location?.latitude,
            longitude = domain.location?.longitude,
            areaId = domain.areaId,
            regionId = domain.regionId,
            totalFloorArea = domain.totalFloorArea,
            hasElevator = domain.hasElevator,
            parkingInfo = domain.parkingInfo,
            buildingSize = domain.buildingSize
        )

        // 도메인 모델의 통계 정보를 엔티티로 변환
        domain.statistics.forEach { statistic ->
            val statisticEntity = toBuildingStatisticEntity(statistic, entity)
            entity.addStatistic(statisticEntity)
        }

        return entity
    }

    /**
     * BuildingStatisticEntity를 BuildingStatistic 도메인 모델로 변환
     */
    fun toBuildingStatistic(entity: BuildingStatisticEntity, buildingId: UniqueId): BuildingStatistic {
        return BuildingStatistic.create(
            id = entity.id,
            buildingId = buildingId,
            collectedAt = entity.collectedAt,
            demographic = toDemographicStatistic(entity.demographicInfo),
            transportation = toTransportationStatistic(entity.transportationInfo),
            social = toSocialStatistic(entity.socialInfo),
            review = toReviewStatistic(entity.reviewInfo)
        )
    }

    /**
     * BuildingStatistic 도메인 모델을 BuildingStatisticEntity로 변환
     */
    fun toBuildingStatisticEntity(domain: BuildingStatistic, buildingEntity: BuildingEntity): BuildingStatisticEntity {
        val entity = BuildingStatisticEntity(
            id = domain.id,
            collectedAt = domain.collectedAt,
            demographicInfo = toDemographicInfo(domain.demographic),
            transportationInfo = toTransportationInfo(domain.transportation),
            socialInfo = toSocialInfo(domain.social),
            reviewInfo = toReviewInfo(domain.review)
        )
        entity.building = buildingEntity
        return entity
    }

    /**
     * DemographicInfo 변환
     */
    private fun toDemographicStatistic(entity: DemographicInfo): DemographicStatistic {
        return DemographicStatistic(
            totalVisitorCount = entity.totalVisitorCount,
            floatingPopulation = entity.floatingPopulation,
            weekdayPeakHourPopulation = entity.weekdayPeakHourPopulation,
            weekendPeakHourPopulation = entity.weekendPeakHourPopulation,
            storeCount = entity.storeCount
        )
    }

    private fun toDemographicInfo(domain: DemographicStatistic): DemographicInfo {
        return DemographicInfo(
            totalVisitorCount = domain.totalVisitorCount,
            floatingPopulation = domain.floatingPopulation,
            weekdayPeakHourPopulation = domain.weekdayPeakHourPopulation,
            weekendPeakHourPopulation = domain.weekendPeakHourPopulation,
            storeCount = domain.storeCount
        )
    }

    /**
     * TransportationInfo 변환
     */
    private fun toTransportationStatistic(entity: TransportationInfo): TransportationStatistic {
        return TransportationStatistic(
            distanceToStation = entity.distanceToStation,
            publicTransportUsers = entity.publicTransportUsers,
            transportationUsage = entity.transportationUsage,
            nearbyBusStopCount = entity.nearbyBusStopCount,
            nearbySubwayStationCount = entity.nearbySubwayStationCount
        )
    }

    private fun toTransportationInfo(domain: TransportationStatistic): TransportationInfo {
        return TransportationInfo(
            distanceToStation = domain.distanceToStation,
            publicTransportUsers = domain.publicTransportUsers,
            transportationUsage = domain.transportationUsage,
            nearbyBusStopCount = domain.nearbyBusStopCount,
            nearbySubwayStationCount = domain.nearbySubwayStationCount
        )
    }

    /**
     * SocialInfo 변환
     */
    private fun toSocialStatistic(entity: SocialInfo): SocialStatistic {
        return SocialStatistic(
            snsMentionCount = entity.snsMentionCount,
            hashtagUsageCount = entity.hashtagUsageCount,
            keywordSearchCount = entity.keywordSearchCount,
            topKeywords = entity.topKeywords,
            topHashtags = entity.topHashtags,
            instagramPostCount = entity.instagramPostCount,
            newsArticleCount = entity.newsArticleCount
        )
    }

    private fun toSocialInfo(domain: SocialStatistic): SocialInfo {
        return SocialInfo(
            snsMentionCount = domain.snsMentionCount,
            hashtagUsageCount = domain.hashtagUsageCount,
            keywordSearchCount = domain.keywordSearchCount,
            topKeywords = domain.topKeywords,
            topHashtags = domain.topHashtags,
            instagramPostCount = domain.instagramPostCount,
            newsArticleCount = domain.newsArticleCount
        )
    }

    /**
     * ReviewInfo 변환
     */
    private fun toReviewStatistic(entity: ReviewInfo): ReviewStatistic {
        return ReviewStatistic(
            averageRating = entity.averageRating,
            reviewCount = entity.reviewCount,
            positiveSentimentRatio = entity.positiveSentimentRatio,
            negativeSentimentRatio = entity.negativeSentimentRatio,
            recentReviews = entity.recentReviews
        )
    }

    private fun toReviewInfo(domain: ReviewStatistic): ReviewInfo {
        return ReviewInfo(
            averageRating = domain.averageRating,
            reviewCount = domain.reviewCount,
            positiveSentimentRatio = domain.positiveSentimentRatio,
            negativeSentimentRatio = domain.negativeSentimentRatio,
            recentReviews = domain.recentReviews
        )
    }
} 