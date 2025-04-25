package com.wheretopop.infrastructure.social

import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.enums.SocialMediaType
import com.wheretopop.shared.enums.SocialMediaCategory
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * 소셜 미디어 정의 엔티티
 * 소셜 미디어의 유형과 카테고리를 정의하는 엔티티
 */
@Table("social_media")
data class SocialMediaEntity(
    @Id
    @Column("id")
    val id: Long,

    @Column("name")
    val name: String,

    @Column("type")
    val type: SocialMediaType,

    @Column("category")
    val category: SocialMediaCategory,

    @Column("description")
    val description: String?,

    @Column("base_url")
    val baseUrl: String?,

    @Column("created_at")
    val createdAt: LocalDateTime,

    @Column("updated_at")
    val updatedAt: LocalDateTime,

    @Column("deleted_at")
    val deletedAt: LocalDateTime?
) {
    // companion object {
    //     fun of(socialMedia: SocialMedia): SocialMediaEntity {
    //         return SocialMediaEntity(
    //             id = socialMedia.id.toLong(),
    //             name = socialMedia.name,
    //             type = socialMedia.type,
    //             category = socialMedia.category,
    //             description = socialMedia.description,
    //             baseUrl = socialMedia.baseUrl,
    //             createdAt = socialMedia.createdAt,
    //             updatedAt = socialMedia.updatedAt,
    //             deletedAt = socialMedia.deletedAt
    //         )
    //     }
    // }

    // fun toDomain(): SocialMedia {
    //     return SocialMedia.create(
    //         id = UniqueId.of(id),
    //         name = name,
    //         type = type,
    //         category = category,
    //         description = description,
    //         baseUrl = baseUrl
    //     )
    // }

    // fun update(socialMedia: SocialMedia): SocialMediaEntity {
    //     return copy(
    //         name = socialMedia.name,
    //         type = socialMedia.type,
    //         category = socialMedia.category,
    //         description = socialMedia.description,
    //         baseUrl = socialMedia.baseUrl,
    //         updatedAt = LocalDateTime.now()
    //     )
    // }
}
