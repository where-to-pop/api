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
 * 리뷰 유형 소셜 미디어 반응 스냅샷 엔티티
 * (맛집 리뷰, 장소 리뷰, 상품 리뷰 등)
 */
@Entity
@Table(
    name = "area_review_reaction",
    indexes = [
        Index(name = "idx_area_review_reaction_area_id", columnList = "area_id"),
        Index(name = "idx_area_review_reaction_social_media_id", columnList = "social_media_id"),
        Index(name = "idx_area_review_reaction_captured_at", columnList = "captured_at")
    ]
)
@Comment("지역별 리뷰 반응 정보 테이블")
class ReviewReactionEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Convert(converter = UniqueIdConverter::class)
    @Comment("리뷰 반응 고유 식별자 (Snowflake ID)")
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
    
    @Column(name = "review_count")
    @Comment("리뷰 수")
    var reviewCount: Int = 0,
    
    @Column(name = "avg_rating", columnDefinition = "DECIMAL(3,2)")
    @Comment("평균 평점 (0.0-5.0)")
    var avgRating: Double? = null,
    
    @Column(name = "five_star_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("5점 평가 비율 (%)")
    var fiveStarRatio: Double? = null,
    
    @Column(name = "one_star_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("1점 평가 비율 (%)")
    var oneStarRatio: Double? = null,
    
    @Column(name = "avg_review_length")
    @Comment("평균 리뷰 길이 (글자)")
    var avgReviewLength: Int? = null,
    
    @Column(name = "photo_included_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("사진 포함 비율 (%)")
    var photoIncludedRatio: Double? = null,
    
    @Column(name = "revisit_intention_ratio", columnDefinition = "DECIMAL(5,2)")
    @Comment("재방문 의사 비율 (%)")
    var revisitIntentionRatio: Double? = null,
    
    @Column(name = "top_keywords", columnDefinition = "TEXT")
    @Comment("인기 키워드 (JSON 형식)")
    var topKeywords: String? = null,
    
    @Column(name = "menu_mention_count")
    @Comment("메뉴 언급 수 (음식점 리뷰)")
    var menuMentionCount: Int? = null,
    
    @Column(name = "price_mention_count")
    @Comment("가격 언급 수")
    var priceMentionCount: Int? = null,
    
    @Column(name = "service_mention_count")
    @Comment("서비스 품질 언급 수")
    var serviceMentionCount: Int? = null,
    
    @Column(name = "atmosphere_mention_count")
    @Comment("분위기 언급 수")
    var atmosphereMentionCount: Int? = null,
    
) : AbstractEntity() 