package com.wheretopop.infrastructure.popup

import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.Gender
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import com.wheretopop.shared.model.AbstractEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.ZonedDateTime

@Entity
@Table(name = "popups")
@Comment("팝업 스토어 정보 테이블")
class PopupEntity private constructor(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("팝업 스토어 고유 식별자")
    val id: Long,

    @Column(name = "name", nullable = false)
    @Comment("팝업 스토어 이름")
    val name: String,

    @Column(name = "building_id")
    @Comment("건물 ID (FK - buildings 테이블)")
    val buildingId: Long?,

    @Column(name = "start_time")
    @Comment("팝업 스토어 시작 시간")
    val startTime: ZonedDateTime?,

    @Column(name = "end_time")
    @Comment("팝업 스토어 종료 시간")
    private var _endTime: ZonedDateTime?,

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    @Comment("팝업 카테고리 (FASHION, FOOD_AND_BEVERAGE, BEAUTY, ART, CHARACTER, MEDIA, OTHER)")
    val category: PopUpCategory?,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @Comment("팝업 타입 (RETAIL, EXHIBITION, BRANDING, OTHER)")
    val type: PopUpType?,

    @Column(name = "description")
    @Comment("팝업 스토어 설명")
    val description: String?,

    @Column(name = "brand_name")
    @Comment("브랜드 이름")
    val brandName: String?,
        
    @Column(name = "visitor_count")
    @Comment("방문자 수")
    val visitorCount: Int?,
        
    @Column(name = "visitor_age_group")
    @Comment("방문자 연령대 분포 (JSON 형식)")
    val visitorAgeGroup: String?,
        
    @Column(name = "visitor_gender_ratio")
    @Comment("방문자 성별 비율 (JSON 형식)")
    val visitorGenderRatio: String?,
        
    @Column(name = "news_mention_count")
    @Comment("뉴스 언급 횟수")
    val newsMentionCount: Int?,
        
    @Column(name = "hashtag_usage_count")
    @Comment("해시태그 사용 횟수")
    val hashtagUsageCount: Int?,
        
    @Column(name = "keyword_search_count")
    @Comment("키워드 검색 횟수")
    val keywordSearchCount: Int?,
        
    @Column(name = "positive_review_ratio")
    @Comment("긍정적 리뷰 비율 (%)")
    val positiveReviewRatio: Double?
) : AbstractEntity() {

    val endTime: ZonedDateTime?
        get() = _endTime

    companion object {
        fun create(
            id: Long,
            name: String,
            buildingId: Long? = null,
            startTime: ZonedDateTime? = null,
            endTime: ZonedDateTime? = null,
            category: PopUpCategory? = null,
            type: PopUpType? = null,
            description: String? = null,
            brandName: String? = null,
            visitorCount: Int? = null,
            visitorAgeGroup: String? = null,
            visitorGenderRatio: String? = null,
            newsMentionCount: Int? = null,
            hashtagUsageCount: Int? = null,
            keywordSearchCount: Int? = null,
            positiveReviewRatio: Double? = null
        ): PopupEntity {
            require(name.isNotBlank()) { "name must not be blank" }
            
            // 시작 시간과 종료 시간이 모두 있는 경우에만 검증
            if (startTime != null && endTime != null) {
                require(startTime.isBefore(endTime)) { "startTime must be before endTime" }
            }
            
            return PopupEntity(
                id = id,
                name = name,
                buildingId = buildingId,
                startTime = startTime,
                _endTime = endTime,
                category = category,
                type = type,
                description = description,
                brandName = brandName,
                visitorCount = visitorCount,
                visitorAgeGroup = visitorAgeGroup,
                visitorGenderRatio = visitorGenderRatio,
                newsMentionCount = newsMentionCount,
                hashtagUsageCount = hashtagUsageCount,
                keywordSearchCount = keywordSearchCount,
                positiveReviewRatio = positiveReviewRatio
            )
        }
    }

    fun changeEndTime(newEndTime: ZonedDateTime?) {
        val currentStartTime = this.startTime
        if (currentStartTime != null && newEndTime != null) {
            require(currentStartTime.isBefore(newEndTime)) { "endTime must be after startTime" }
        }
        this._endTime = newEndTime
    }
}
