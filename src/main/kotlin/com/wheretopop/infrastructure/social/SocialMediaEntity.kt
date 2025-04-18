package com.wheretopop.infrastructure.social

import com.wheretopop.shared.converter.UniqueIdConverter
import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.enums.SocialMediaType
import com.wheretopop.shared.enums.SocialMediaCategory
import jakarta.persistence.*
import org.hibernate.annotations.Comment

/**
 * 소셜 미디어 정의 엔티티
 * 소셜 미디어의 유형과 카테고리를 정의하는 엔티티
 */
@Entity
@Table(
    name = "social_media",
    indexes = [
        Index(name = "idx_social_media_type", columnList = "type"),
        Index(name = "idx_social_media_category", columnList = "category")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uq_social_media_name", columnNames = ["name"])
    ]
)
@Comment("소셜 미디어 정의 테이블")
class SocialMediaEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Convert(converter = UniqueIdConverter::class)
    @Comment("소셜 미디어 고유 식별자 (Snowflake ID)") 
    var id: UniqueId = UniqueId.create(), 
    
    @Column(name = "name", nullable = false, length = 50)
    @Comment("소셜 미디어 이름") 
    var name: String = "",
    
    @Column(name = "type", nullable = false, length = 20)
    @Comment("소셜 미디어 타입")
    @Enumerated(EnumType.STRING)
    var type: SocialMediaType = SocialMediaType.OTHER,
    
    @Column(name = "category", nullable = false, length = 20)
    @Comment("소셜 미디어 카테고리")
    @Enumerated(EnumType.STRING)
    var category: SocialMediaCategory = SocialMediaCategory.OTHER,
    
    @Column(name = "description")
    @Comment("소셜 미디어 설명") 
    var description: String? = null,
    
    @Column(name = "base_url")
    @Comment("소셜 미디어 기본 URL") 
    var baseUrl: String? = null,

) : AbstractEntity()
