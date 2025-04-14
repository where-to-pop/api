package com.wheretopop.infrastructure.area

import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.Gender
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.model.statistics.AgeDistribution
import com.wheretopop.shared.model.statistics.BrandDistribution
import com.wheretopop.shared.model.statistics.GenderDistribution
import com.wheretopop.shared.model.statistics.GenderRatio
import com.wheretopop.shared.model.statistics.Hashtag
import com.wheretopop.shared.model.statistics.Keyword
import com.wheretopop.shared.model.statistics.StoreCategory
import com.wheretopop.shared.model.statistics.TransportationUsage
import com.wheretopop.shared.model.statistics.VisitorResidence
import com.wheretopop.shared.util.JsonUtil
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

/**
 * 권역 통계 정보를 담은 엔티티 (DB에 반정규화하여 저장)
 * 도메인 특성에 따라 상권 정보, 부동산 정보, SNS 정보 등을 포함함
 */
@Entity
@Table(name = "area_statistics", indexes = [Index(name = "idx_area_statistics_area_id", columnList = "area_id")])
@Comment("권역 통계 정보 테이블")
class AreaStatisticEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("통계 고유 식별자 (Snowflake ID)")
    @JdbcTypeCode(SqlTypes.BIGINT)
    var id: UniqueId = UniqueId.create(),

    @Column(name = "collected_at", nullable = false)
    @Comment("통계 수집 시간")
    var collectedAt: LocalDateTime = LocalDateTime.now(),

    // 상권 기본 정보
    @Embedded
    var commercialInfo: CommercialInfo = CommercialInfo(),

    // 부동산 정보
    @Embedded
    var realEstateInfo: RealEstateInfo = RealEstateInfo(),

    // SNS 및 소셜 정보
    @Embedded
    var socialInfo: SocialInfo = SocialInfo(),

    // 인구통계 정보
    @Embedded
    var demographicInfo: DemographicInfo = DemographicInfo()
) : AbstractEntity() {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    lateinit var area: AreaEntity

    companion object {
        fun create(
            id: UniqueId = UniqueId.create(),
            area: AreaEntity,
            collectedAt: LocalDateTime = LocalDateTime.now(),
            commercialInfo: CommercialInfo = CommercialInfo(),
            realEstateInfo: RealEstateInfo = RealEstateInfo(),
            socialInfo: SocialInfo = SocialInfo(),
            demographicInfo: DemographicInfo = DemographicInfo()
        ): AreaStatisticEntity {
            val statistic = AreaStatisticEntity(
                id = id,
                collectedAt = collectedAt,
                commercialInfo = commercialInfo,
                realEstateInfo = realEstateInfo,
                socialInfo = socialInfo,
                demographicInfo = demographicInfo
            )
            statistic.area = area
            return statistic
        }
    }
}

/**
 * 상권 기본 정보 (매장 수, 신규 매장, 폐점 매장 등)
 */
@Embeddable
class CommercialInfo(
    @Column(name = "com_store_count")
    @Comment("총 매장 수")
    var storeCount: Int? = null,

    @Column(name = "com_new_store_count")
    @Comment("신규 매장 수 (지난 6개월)")
    var newStoreCount: Int? = null,
    
    @Column(name = "com_closed_store_count")
    @Comment("폐점 매장 수 (지난 6개월)")
    var closedStoreCount: Int? = null,
    
    @Column(name = "com_popup_frequency_count")
    @Comment("팝업 발생 횟수 (지난 12개월간)")
    var popupFrequencyCount: Int? = null,
    
    @Column(name = "com_event_count")
    @Comment("이벤트 개최 수")
    var eventCount: Int? = null,
    
    @Column(name = "com_main_store_categories", columnDefinition = "JSON")
    @Comment("주요 매장 카테고리 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var mainStoreCategories: List<StoreCategory>? = null,
    
    @Column(name = "com_brand_distribution", columnDefinition = "JSON")
    @Comment("브랜드 분포 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var brandDistribution: List<BrandDistribution>? = null
)

/**
 * 부동산 관련 정보 (임대료, 공실률 등)
 */
@Embeddable
class RealEstateInfo(
    @Column(name = "re_average_rent")
    @Comment("평균 임대료 (원/m²)")
    var averageRent: Long? = null,

    @Column(name = "re_average_vacancy_rate", columnDefinition = "DECIMAL(5,2)")
    @Comment("평균 공실률 (%)")
    var averageVacancyRate: Double? = null,
    
    @Column(name = "re_min_rent")
    @Comment("최소 임대료 (원/m²)")
    var minRent: Long? = null,
    
    @Column(name = "re_max_rent")
    @Comment("최대 임대료 (원/m²)")
    var maxRent: Long? = null,
    
    @Column(name = "re_recent_price_trend", columnDefinition = "DECIMAL(5,2)")
    @Comment("최근 가격 추세 (3개월 변화율, %)")
    var recentPriceTrend: Double? = null,
    
    @Column(name = "re_building_count")
    @Comment("건물 수")
    var buildingCount: Int? = null,
    
    @Column(name = "re_average_building_age", columnDefinition = "DECIMAL(5,1)")
    @Comment("평균 건물 연식 (년)")
    var averageBuildingAge: Double? = null
)

/**
 * SNS 및 소셜 관련 정보 (언급, 해시태그, 감성분석 등)
 */
@Embeddable
class SocialInfo(
    @Column(name = "soc_sns_mention_count")
    @Comment("SNS 언급 횟수")
    var snsMentionCount: Int? = null,
    
    @Column(name = "soc_positive_sentiment_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("긍정적 감성 비율 (%)")
    var positiveSentimentRatio: Double? = null,
    
    @Column(name = "soc_negative_sentiment_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("부정적 감성 비율 (%)")
    var negativeSentimentRatio: Double? = null,
    
    @Column(name = "soc_neutral_sentiment_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("중립적 감성 비율 (%)")
    var neutralSentimentRatio: Double? = null,
    
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
    
    @Column(name = "soc_blog_post_count")
    @Comment("블로그 게시물 수")
    var blogPostCount: Int? = null,
    
    @Column(name = "soc_news_article_count")
    @Comment("뉴스 기사 수")
    var newsArticleCount: Int? = null
)

/**
 * 인구통계 관련 정보 (유동인구, 인구밀도 등)
 */
@Embeddable
class DemographicInfo(
    @Column(name = "demo_floating_population")
    @Comment("일평균 유동인구 수")
    var floatingPopulation: Int? = null,
    
    @Column(name = "demo_population_density_value")
    @Comment("인구 밀도 (명/km²)")
    var populationDensityValue: Int? = null,
    
    @Column(name = "demo_weekday_peak_hour_population")
    @Comment("평일 최고 시간대 유동인구")
    var weekdayPeakHourPopulation: Int? = null,
    
    @Column(name = "demo_weekend_peak_hour_population")
    @Comment("주말 최고 시간대 유동인구")
    var weekendPeakHourPopulation: Int? = null,
    
    @Column(name = "demo_age_distribution", columnDefinition = "JSON")
    @Comment("연령대별 분포 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var ageDistribution: List<AgeDistribution>? = null,
    
    @Column(name = "demo_gender_ratio", columnDefinition = "JSON")
    @Comment("성별 비율 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var genderRatio: List<GenderRatio>? = null,
    
    @Column(name = "demo_visitor_residence_distribution", columnDefinition = "JSON")
    @Comment("방문자 거주지 분포 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var visitorResidenceDistribution: List<VisitorResidence>? = null,
    
    @Column(name = "demo_transportation_usage", columnDefinition = "JSON")
    @Comment("교통수단 이용 비율 (JSON 형식)")
    @JdbcTypeCode(SqlTypes.JSON)
    var transportationUsage: List<TransportationUsage>? = null
) 