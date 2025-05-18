package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.JpaConverterConfig
import com.wheretopop.domain.popup.PopupInfo
import com.wheretopop.infrastructure.popup.external.popply.PopupDetail
import com.wheretopop.shared.domain.identifier.PopupPopplyId
import com.wheretopop.shared.model.UniqueId
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant



/**
 * 팝업 Popply 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "popup_popply")
@EntityListeners(AuditingEntityListener::class)
class PopupPopplyEntity(
    @Id
    @Convert(converter = JpaConverterConfig.PopupPopplyIdConverter::class)
    val id: PopupPopplyId = PopupPopplyId.create(),

    @Column(name = "popup_id", nullable = false)
    val popupId: Long,

    @Column(name = "popup_name", nullable = false)
    val popupName: String,

    @Column(name = "address", nullable = false)
    val address: String,

    @Column(name = "optional_address")
    val optionalAddress: String? = null,

    @Column(name = "start_date")
    val startDate: Instant? = null,

    @Column(name = "end_date")
    val endDate: Instant? = null,

    @Column(name = "description", nullable = false)
    val description: String,

    @Column(name = "url")
    val url: String? = null,

    @Column(name = "latitude")
    val latitude: Double? = null,

    @Column(name = "longitude")
    val longitude: Double? = null,

    @Column(name = "organizer_name")
    val organizerName: String? = null,

    @Column(name = "organizer_url")
    val organizerUrl: String? = null,

    @Column(name = "popply_id", nullable = false)
    val popplyId: Int,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
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
                popplyId = popupDetail.popplyId
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