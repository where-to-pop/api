package com.wheretopop.infrastructure.region

import com.wheretopop.shared.model.AbstractEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "regions")
@Comment("지역 정보 테이블")
class RegionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Comment("지역 고유 식별자")
    var id: Long = 0,

    @Column(name = "name", nullable = false)
    @Comment("지역 이름")
    var name: String
) : AbstractEntity() {

    companion object {
        fun create(
            name: String,
            id: Long = 0
        ): RegionEntity {
            require(name.isNotBlank()) { "name must not be blank" }
            
            return RegionEntity(
                id = id,
                name = name
            )
        }
    }
}