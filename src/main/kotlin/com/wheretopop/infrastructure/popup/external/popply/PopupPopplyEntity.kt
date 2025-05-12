package com.wheretopop.infrastructure.popup.external.popply

import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.shared.model.UniqueId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

class PopupPopplyId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): PopupPopplyId {
            return PopupPopplyId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): PopupPopplyId {
            return PopupPopplyId(UniqueId.of(value).value)
        }
    }
}

@Table("popup_popply")
data class PopupPopplyEntity(
    @Id
    @Column("id")
    val id: PopupPopplyId = PopupPopplyId.create(),

    @Column("popup_id")
    val popupId: Long,

    @Column("popup_name")
    val popupName: String,

    @Column("address")
    val address: String,

    @Column("optional_address")
    val optionalAddress: String? = null,

    @Column("start_date")
    val startDate: Instant? = null,

    @Column("end_date")
    val endDate: Instant? = null,

    @Column("description")
    val description: String,

    @Column("url")
    val url: String? = null,

    @Column("latitude")
    val latitude: Double? = null,

    @Column("longitude")
    val longitude: Double? = null,

    @Column("organizer_name")
    val organizerName: String? = null,

    @Column("organizer_url")
    val organizerUrl: String? = null,

    @Column("popply_id")
    val popplyId: Int,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),

    @Column("deleted_at")
    val deletedAt: Instant? = null

) {
    companion object {
        fun of(popupDetail: PopupDetail, popupId: Long): PopupPopplyEntity {
            return PopupPopplyEntity(
                id = PopupPopplyId.create(),
                popupId = popupId,
                popupName = popupDetail.name,
                address = popupDetail.address,
                optionalAddress = popupDetail.optionalAddress,
                startDate = popupDetail.startDate,
                endDate = popupDetail.endDate,
                description = popupDetail.description,
                url = popupDetail.url,
                latitude = popupDetail.latitude,
                longitude = popupDetail.longitude,
                organizerName = popupDetail.organizerName,
                organizerUrl = popupDetail.organizerUrl,
                popplyId = popupDetail.popplyId,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        }

        fun toDomain(entity: PopupPopplyEntity): PopupInfo {
            return PopupInfo(
                id = entity.popupId,
                name = entity.popupName,
                address = entity.address,
                description = entity.description,
                organizerName = entity.organizerName ?: ""
            )
        }
    }
}

@WritingConverter
class PopupPopplyIdToLongConverter : Converter<PopupPopplyId, Long> {
    override fun convert(source: PopupPopplyId) = source.toLong()
}

@ReadingConverter
class LongToPopupPopplyIdConverter : Converter<Long, PopupPopplyId> {
    override fun convert(source: Long) = PopupPopplyId.of(source)
}