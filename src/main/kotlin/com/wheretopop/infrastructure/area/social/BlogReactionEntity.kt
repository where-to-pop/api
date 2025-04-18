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
 * 블로그 유형 소셜 미디어 반응 스냅샷 엔티티
 * (네이버 블로그, 티스토리, 브런치 등)
 */
@Entity
@Table(
    name = "area_blog_reaction",
    indexes = [
        Index(name = "idx_area_blog_reaction_area_id", columnList = "area_id"),
        Index(name = "idx_area_blog_reaction_social_media_id", columnList = "social_media_id"),
        Index(name = "idx_area_blog_reaction_captured_at", columnList = "captured_at")
    ]
)
@Comment("지역별 블로그 반응 정보 테이블")
class BlogReactionEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Convert(converter = UniqueIdConverter::class)
    @Comment("블로그 반응 고유 식별자 (Snowflake ID)")
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
    
    @Column(name = "post_count")
    @Comment("게시물 수")
    var postCount: Int = 0,
    
    @Column(name = "comment_count")
    @Comment("댓글 수")
    var commentCount: Int = 0,
    
    @Column(name = "avg_post_length")
    @Comment("평균 포스트 길이 (글자)")
    var avgPostLength: Int? = null,
    
    @Column(name = "avg_view_count")
    @Comment("평균 조회수")
    var avgViewCount: Int? = null,
    
    @Column(name = "active_blogger_count")
    @Comment("활동 블로거 수")
    var activeBloggerCount: Int? = null,
    
    @Column(name = "keyword_density", columnDefinition = "DECIMAL(5,2)")
    @Comment("키워드 밀집도 (%)")
    var keywordDensity: Double? = null,
    
    @Column(name = "top_keywords", columnDefinition = "TEXT")
    @Comment("인기 키워드 (JSON 형식)")
    var topKeywords: String? = null,
    
    @Column(name = "link_count")
    @Comment("외부 링크 수")
    var linkCount: Int? = null,
    
    @Column(name = "image_count")
    @Comment("이미지 수")
    var imageCount: Int? = null,
    
    @Column(name = "series_post_count")
    @Comment("시리즈 포스트 수")
    var seriesPostCount: Int? = null,

) : AbstractEntity() 