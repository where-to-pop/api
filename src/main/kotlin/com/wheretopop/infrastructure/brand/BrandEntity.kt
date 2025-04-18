package com.wheretopop.infrastructure.brand

import com.wheretopop.shared.model.AbstractEntity
import com.wheretopop.shared.model.UniqueId
import com.wheretopop.shared.converter.UniqueIdConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment

/**
 * 브랜드 정보 엔티티
 */
@Entity
@Table(name = "brand", 
    indexes = [
        Index(name = "idx_brand_name", columnList = "name")
    ]
)
@Comment("브랜드 정보 테이블")
class BrandEntity(
    @Id
    @Column(name = "id", nullable = false)
    @Comment("브랜드 정보 고유 식별자 (Snowflake ID)")
    @Convert(converter = UniqueIdConverter::class)
    val id: UniqueId = UniqueId.create(),

    @Column(name = "name", nullable = false)
    @Comment("브랜드 이름")
    val name: String,

    @Column(name = "category")
    @Comment("브랜드 카테고리 (예: 패션, 식품)")
    val category: String? = null
) : AbstractEntity() {

    // JPA를 위한 기본 생성자
    protected constructor() : this(
        name = "",
        category = null
    )
} 