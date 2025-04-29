package com.wheretopop.infrastructure.building.core

import com.wheretopop.domain.building.core.Building
import com.wheretopop.domain.building.core.BuildingId
import com.wheretopop.shared.model.Location
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("buildings")
internal class BuildingEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: BuildingId,
    @Column("address")
    val address: String,
    @Column("latitude")
    val latitude: Double,
    @Column("longitude")
    val longitude: Double,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant?
) {
    companion object {
        fun of(building: Building): BuildingEntity {
            return BuildingEntity(
                id = building.id,
                address = building.address,
                latitude = building.location.latitude,
                longitude = building.location.longitude,
                createdAt = building.createdAt,
                updatedAt = building.updatedAt,
                deletedAt = building.deletedAt
            )
        }
    }

    fun toDomain(): Building {
        return Building.create(
            id = id,
            address = address,
            location = Location(latitude, longitude),
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
    }

    fun update(building: Building): BuildingEntity {
        return BuildingEntity(
            id = id,
            address = building.address,
            latitude = building.location.latitude,
            longitude = building.location.longitude,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
}

@WritingConverter
class BuildingIdToLongConverter : Converter<BuildingId, Long> {
    override fun convert(source: BuildingId) = source.toLong()
}


@ReadingConverter
class LongToBuildingIdConverter : Converter<Long, BuildingId> {
    override fun convert(source: Long) = BuildingId.of(source)
}

