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
 * SNS 유형 소셜 미디어 반응 스냅샷 엔티티
 * (Facebook, Instagram, Twitter 등)
 */
@Entity
@Table(
    name = "area_sns_reaction",
    indexes = [
        Index(name = "idx_area_sns_reaction_area_id", columnList = "area_id"),
        Index(name = "idx_area_sns_reaction_social_media_id", columnList = "social_media_id"),
        Index(name = "idx_area_sns_reaction_captured_at", columnList = "captured_at")
    ]
)
@Comment("지역별 SNS 반응 정보 테이블")
class SNSReactionEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Convert(converter = UniqueIdConverter::class)
    @Comment("SNS 반응 고유 식별자 (Snowflake ID)")
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
    
    @Column(name = "like_count")
    @Comment("좋아요 수")
    var likeCount: Int = 0,
    
    @Column(name = "comment_count")
    @Comment("댓글 수")
    var commentCount: Int = 0,
    
    @Column(name = "share_count")
    @Comment("공유 수")
    var shareCount: Int = 0,
    
    @Column(name = "hashtag_count")
    @Comment("해시태그 사용 수")
    var hashtagCount: Int = 0,
    
    @Column(name = "top_hashtags", columnDefinition = "TEXT")
    @Comment("인기 해시태그 (JSON 형식)")
    var topHashtags: String? = null,
    
    @Column(name = "user_demographic", columnDefinition = "TEXT")
    @Comment("사용자 인구통계 정보 (JSON 형식)")
    var userDemographic: String? = null,
    
    @Column(name = "engagement_rate", columnDefinition = "DECIMAL(5,2)")
    @Comment("참여율 (%)")
    var engagementRate: Double? = null,
    

) : AbstractEntity() 