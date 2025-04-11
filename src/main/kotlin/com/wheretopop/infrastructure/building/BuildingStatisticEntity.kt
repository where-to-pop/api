package com.wheretopop.infrastructure.building

import com.wheretopop.shared.enums.FloatingPopulation
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.model.statistics.Hashtag
import com.wheretopop.shared.model.statistics.Keyword
import com.wheretopop.shared.model.statistics.Review
import com.wheretopop.shared.model.statistics.TransportationUsage
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

/**
 * 건물 통계 정보를 담은 엔티티 (DB에 반정규화하여 저장)
 * 유동인구, 교통, 소셜, 리뷰 등의 정보를 포함함
 */
@Entity
@Table(name = "building_statistics", indexes = [Index(name = "idx_building_statistics_building_id", columnList = "building_id")])
@Comment("건물 통계 정보 테이블")
class BuildingStatisticEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("통계 고유 식별자 (Snowflake ID)")
    @JdbcTypeCode(SqlTypes.BIGINT)
    var id: UniqueId = UniqueId.create(),

    @Column(name = "collected_at", nullable = false)
    @Comment("통계 수집 시간")
    var collectedAt: LocalDateTime = LocalDateTime.now(),
    
    // 인구통계 정보
    @Embedded
    var demographicInfo: DemographicInfo = DemographicInfo(),
    
    // 교통 관련 정보
    @Embedded
    var transportationInfo: TransportationInfo = TransportationInfo(),
    
    // SNS 및 소셜 정보
    @Embedded
    var socialInfo: SocialInfo = SocialInfo(),
    
    // 리뷰 정보
    @Embedded
    var reviewInfo: ReviewInfo = ReviewInfo()
) : AbstractEntity() {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id", nullable = false)
    @Comment("건물 정보")
    lateinit var building: BuildingEntity

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            building: BuildingEntity,
            collectedAt: LocalDateTime = LocalDateTime.now(),
            demographicInfo: DemographicInfo = DemographicInfo(),
            transportationInfo: TransportationInfo = TransportationInfo(),
            socialInfo: SocialInfo = SocialInfo(),
            reviewInfo: ReviewInfo = ReviewInfo()
        ): BuildingStatisticEntity {
            val statistic = BuildingStatisticEntity(
                id = id,
                collectedAt = collectedAt,
                demographicInfo = demographicInfo,
                transportationInfo = transportationInfo,
                socialInfo = socialInfo,
                reviewInfo = reviewInfo
            )
            statistic.building = building
            return statistic
        }
    }
}

/**
 * 인구통계 관련 정보
 */
@Embeddable
class DemographicInfo(
    @Column(name = "demo_total_visitor_count")
    @Comment("총 방문객 수")
    var totalVisitorCount: Int? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "demo_floating_population")
    @Comment("유동인구 수준 (VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH)")
    var floatingPopulation: FloatingPopulation? = null,
    
    @Column(name = "demo_weekday_peak_hour_population")
    @Comment("평일 최대 시간대 유동인구")
    var weekdayPeakHourPopulation: Int? = null,
    
    @Column(name = "demo_weekend_peak_hour_population")
    @Comment("주말 최대 시간대 유동인구")
    var weekendPeakHourPopulation: Int? = null,
    
    @Column(name = "demo_store_count")
    @Comment("건물 내 매장 수")
    var storeCount: Int? = null
)

/**
 * 교통 관련 정보
 */
@Embeddable
class TransportationInfo(
    @Column(name = "trans_distance_to_station")
    @Comment("역과의 거리 (m)")
    var distanceToStation: Double? = null,
    
    @Column(name = "trans_public_transport_users")
    @Comment("대중교통 이용객 수")
    var publicTransportUsers: Int? = null,
    
    @Column(name = "trans_usage", columnDefinition = "JSON")
    @Comment("대중교통 이용 통계 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var transportationUsage: TransportationUsage? = null,
    
    @Column(name = "trans_nearby_bus_stop_count")
    @Comment("인근 버스 정류장 수 (500m 이내)")
    var nearbyBusStopCount: Int? = null,
    
    @Column(name = "trans_nearby_subway_station_count")
    @Comment("인근 지하철역 수 (500m 이내)")
    var nearbySubwayStationCount: Int? = null
)

/**
 * SNS 및 소셜 관련 정보
 */
@Embeddable
class SocialInfo(
    @Column(name = "soc_sns_mention_count")
    @Comment("SNS 언급 횟수")
    var snsMentionCount: Int? = null,
    
    @Column(name = "soc_hashtag_usage_count")
    @Comment("해시태그 사용 횟수")
    var hashtagUsageCount: Int? = null,
    
    @Column(name = "soc_keyword_search_count")
    @Comment("키워드 검색 횟수")
    var keywordSearchCount: Int? = null,
    
    @Column(name = "soc_top_keywords", columnDefinition = "JSON")
    @Comment("상위 키워드 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var topKeywords: List<Keyword>? = null,
    
    @Column(name = "soc_top_hashtags", columnDefinition = "JSON")
    @Comment("상위 해시태그 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var topHashtags: List<Hashtag>? = null,
    
    @Column(name = "soc_instagram_post_count")
    @Comment("인스타그램 게시물 수")
    var instagramPostCount: Int? = null,
    
    @Column(name = "soc_news_article_count")
    @Comment("뉴스 기사 수")
    var newsArticleCount: Int? = null
)

/**
 * 리뷰 관련 정보
 */
@Embeddable
class ReviewInfo(
    @Column(name = "rev_average_rating", columnDefinition = "DECIMAL(3,2)")
    @Comment("평균 평점 (5점 만점)")
    var averageRating: Double? = null,
    
    @Column(name = "rev_review_count")
    @Comment("총 리뷰 수")
    var reviewCount: Int? = null,
    
    @Column(name = "rev_positive_sentiment_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("긍정적 감성 비율 (%)")
    var positiveSentimentRatio: Double? = null,
    
    @Column(name = "rev_negative_sentiment_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("부정적 감성 비율 (%)")
    var negativeSentimentRatio: Double? = null,
    
    @Column(name = "rev_recent_reviews", columnDefinition = "JSON")
    @Comment("최근 리뷰 요약 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var recentReviews: List<Review>? = null
) 