package com.wheretopop.infrastructure.area.social

import com.wheretopop.infrastructure.area.AreaEntity
import com.wheretopop.infrastructure.social.SocialMediaEntity
import com.wheretopop.shared.converter.UniqueIdConverter
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

/**
 * 뉴스 유형 소셜 미디어 반응 스냅샷 엔티티
 * (네이버 뉴스, 다음 뉴스, 언론사 기사 등)
 */
@Entity
@Table(
    name = "area_news_reaction",
    indexes = [
        Index(name = "idx_area_news_reaction_area_id", columnList = "area_id"),
        Index(name = "idx_area_news_reaction_social_media_id", columnList = "social_media_id"),
        Index(name = "idx_area_news_reaction_captured_at", columnList = "captured_at")
    ]
)
@Comment("지역별 뉴스 반응 정보 테이블")
class NewsReactionEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Convert(converter = UniqueIdConverter::class)
    @Comment("뉴스 반응 고유 식별자 (Snowflake ID)")
    var id: UniqueId = UniqueId.create(),
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    @Comment("권역 정보")
    var area: AreaEntity,
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "social_media_id", nullable = false)
    @Comment("소셜 미디어 정보")
    var socialMedia: SocialMediaEntity,
    
    @Column(name = "mention_count", nullable = false)
    @Comment("언급 횟수")
    var mentionCount: Int = 0,
    
    @Column(name = "positive_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("긍정적 반응 비율 (%)")
    var positiveRatio: Double? = null,
    
    @Column(name = "negative_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("부정적 반응 비율 (%)")
    var negativeRatio: Double? = null,
    
    @Column(name = "neutral_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("중립적 반응 비율 (%)")
    var neutralRatio: Double? = null,
    
    @Column(name = "article_count")
    @Comment("기사 수")
    var articleCount: Int = 0,
    
    @Column(name = "publisher_count")
    @Comment("발행 언론사 수")
    var publisherCount: Int? = null,
    
    @Column(name = "top_publishers", columnDefinition = "TEXT")
    @Comment("상위 언론사 (JSON 형식)")
    var topPublishers: String? = null,
    
    @Column(name = "avg_article_length")
    @Comment("평균 기사 길이 (글자)")
    var avgArticleLength: Int? = null,
    
    @Column(name = "multimedia_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("멀티미디어 포함 비율 (%)")
    var multimediaRatio: Double? = null,
    
    @Column(name = "topic_distribution", columnDefinition = "TEXT")
    @Comment("주제별 분포 (JSON 형식)")
    var topicDistribution: String? = null,
    
    @Column(name = "breaking_news_count")
    @Comment("속보 기사 수")
    var breakingNewsCount: Int? = null,
    
    @Column(name = "interview_count")
    @Comment("인터뷰 기사 수")
    var interviewCount: Int? = null,
    
    @Column(name = "reference_count")
    @Comment("참고자료 인용 수")
    var referenceCount: Int? = null,
    
    @Column(name = "expert_opinion_count")
    @Comment("전문가 의견 포함 수")
    var expertOpinionCount: Int? = null,

) : AbstractEntity() 