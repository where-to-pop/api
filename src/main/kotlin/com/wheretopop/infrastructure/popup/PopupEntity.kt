package com.wheretopop.infrastructure.popup

import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime

@Table("popups")
data class PopupEntity(
    @Id
    @Column("id")
    val id: Long,

    @Column("name")
    val name: String,

    @Column("building_id")
    val buildingId: Long?,

    @Column("start_time")
    val startTime: ZonedDateTime?,

    @Column("end_time")
    val endTime: ZonedDateTime?,

    @Column("category")
    val category: PopUpCategory?,
    
    @Column("type")
    val type: PopUpType?,

    @Column("description")
    val description: String?,

    @Column("brand_name")
    val brandName: String?,
        
    @Column("visitor_count")
    val visitorCount: Int?,
        
    @Column("visitor_age_group")
    val visitorAgeGroup: String?,
        
    @Column("visitor_gender_ratio")
    val visitorGenderRatio: String?,
        
    @Column("news_mention_count")
    val newsMentionCount: Int?,
        
    @Column("hashtag_usage_count")
    val hashtagUsageCount: Int?,
        
    @Column("keyword_search_count")
    val keywordSearchCount: Int?,
        
    @Column("positive_review_ratio")
    val positiveReviewRatio: Double?,

    @Column("created_at")
    val createdAt: ZonedDateTime,

    @Column("updated_at")
    val updatedAt: ZonedDateTime,

    @Column("deleted_at")
    val deletedAt: ZonedDateTime?
) {
//    companion object {
//        fun of(popup: Popup): PopupEntity {
//            return PopupEntity(
//                id = popup.id.toLong(),
//                name = popup.name,
//                buildingId = popup.buildingId,
//                startTime = popup.startTime,
//                endTime = popup.endTime,
//                category = popup.category,
//                type = popup.type,
//                description = popup.description,
//                brandName = popup.brandName,
//                visitorCount = popup.visitorCount,
//                visitorAgeGroup = popup.visitorAgeGroup,
//                visitorGenderRatio = popup.visitorGenderRatio,
//                newsMentionCount = popup.newsMentionCount,
//                hashtagUsageCount = popup.hashtagUsageCount,
//                keywordSearchCount = popup.keywordSearchCount,
//                positiveReviewRatio = popup.positiveReviewRatio,
//                createdAt = popup.createdAt,
//                updatedAt = popup.updatedAt,
//                deletedAt = popup.deletedAt
//            )
//        }
//    }
//
//    fun toDomain(): Popup {
//        return Popup.create(
//            id = UniqueId.of(id),
//            name = name,
//            buildingId = buildingId,
//            startTime = startTime,
//            endTime = endTime,
//            category = category,
//            type = type,
//            description = description,
//            brandName = brandName,
//            visitorCount = visitorCount,
//            visitorAgeGroup = visitorAgeGroup,
//            visitorGenderRatio = visitorGenderRatio,
//            newsMentionCount = newsMentionCount,
//            hashtagUsageCount = hashtagUsageCount,
//            keywordSearchCount = keywordSearchCount,
//            positiveReviewRatio = positiveReviewRatio
//        )
//    }
//
//    fun update(popup: Popup): PopupEntity {
//        return copy(
//            name = popup.name,
//            buildingId = popup.buildingId,
//            startTime = popup.startTime,
//            endTime = popup.endTime,
//            category = popup.category,
//            type = popup.type,
//            description = popup.description,
//            brandName = popup.brandName,
//            visitorCount = popup.visitorCount,
//            visitorAgeGroup = popup.visitorAgeGroup,
//            visitorGenderRatio = popup.visitorGenderRatio,
//            newsMentionCount = popup.newsMentionCount,
//            hashtagUsageCount = popup.hashtagUsageCount,
//            keywordSearchCount = popup.keywordSearchCount,
//            positiveReviewRatio = popup.positiveReviewRatio,
//            updatedAt = ZonedDateTime.now()
//        )
//    }
}
