package com.wheretopop.infrastructure.area

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaId
import com.wheretopop.shared.model.Location
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("areas")
internal class AreaEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: AreaId,
    @Column("name")
    val name: String,
    @Column("description")
    val description: String,
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
        fun of(area: Area): AreaEntity {
            return AreaEntity(
                id = area.id,
                name = area.name,
                description = area.description,
                latitude = area.location.latitude,
                longitude = area.location.longitude,
                createdAt = area.createdAt,
                updatedAt = area.updatedAt,
                deletedAt = area.deletedAt
            )
        }
    }

    fun toDomain(): Area {
        return Area.create(
            id = id,
            name = name,
            description = description,
            location = Location(latitude, longitude),
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
    }

    fun update(area: Area): AreaEntity {
        return AreaEntity(
            id = id,
            name = area.name,
            description = area.description,
            latitude = area.location.latitude,
            longitude = area.location.longitude,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
}

@WritingConverter
class AreaIdToLongConverter : Converter<AreaId, Long> {
    override fun convert(source: AreaId) = source.toLong()
}


@ReadingConverter
class LongToAreaIdConverter : Converter<Long, AreaId> {
    override fun convert(source: Long) = AreaId.of(source)
}

