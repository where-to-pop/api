package com.wheretopop.infrastructure.region

import com.wheretopop.shared.model.UniqueId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("regions")
data class RegionEntity(
    @Id
    @Column("id")
    val id: Long,

    @Column("name")
    val name: String,

    @Column("created_at")
    val createdAt: LocalDateTime,

    @Column("updated_at")
    val updatedAt: LocalDateTime,

    @Column("deleted_at")
    val deletedAt: LocalDateTime?
) {
    // companion object {
    //     fun of(region: Region): RegionEntity {
    //         return RegionEntity(
    //             id = region.id.toLong(),
    //             name = region.name,
    //             createdAt = region.createdAt,
    //             updatedAt = region.updatedAt,
    //             deletedAt = region.deletedAt
    //         )
    //     }
    // }

    // fun toDomain(): Region {
    //     return Region.create(
    //         id = UniqueId.of(id),
    //         name = name
    //     )
    // }

    // fun update(region: Region): RegionEntity {
    //     return copy(
    //         name = region.name,
    //         updatedAt = LocalDateTime.now()
    //     )
    // }
}