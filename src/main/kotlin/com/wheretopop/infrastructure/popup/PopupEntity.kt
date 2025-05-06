package com.wheretopop.infrastructure.popup

import com.wheretopop.domain.building.BuildingId
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("popups")
data class PopupEntity(
    @Id
    @Column("id")
    val id: PopupId,

    @Column("building_id")
    val buildingId: Long?,

    @Column("name")
    val name: String,

    @Column("address")
    val address : String,

    @Column("created_at")
    val createdAt: Instant,

    @Column("deleted_at")
    val deletedAt: Instant?,

) {
    companion object {
        fun of(popup: Popup): PopupEntity {
            return PopupEntity(
                id = popup.id,
                name = popup.name,
                buildingId = popup.buildingId,
                address = popup.address,
                createdAt = popup.createdAt,
                deletedAt = popup.deletedAt,
            )
        }
    }

    fun toDomain(): Popup {
        return Popup.create(
            id = id,
            name = name,
            buildingId = buildingId,
            address = address,
            createdAt = createdAt,
            deletedAt = deletedAt,
        )
    }

    fun update(popup: Popup): PopupEntity {
        return PopupEntity(
            id = id,
            name = popup.name,
            buildingId = popup.buildingId,
            address = popup.address,
            createdAt = createdAt,
            deletedAt = deletedAt,
        )
    }
}

@WritingConverter
class PopupIdToLongConverter : Converter<PopupId, Long> {
    override fun convert(source: PopupId) = source.toLong()
}


@ReadingConverter
class LongToPopupIdConverter : Converter<Long, PopupId> {
    override fun convert(source: Long) = PopupId.of(source)
}