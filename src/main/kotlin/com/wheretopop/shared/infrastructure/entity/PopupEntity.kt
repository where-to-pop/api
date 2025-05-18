package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.config.PopupIdConverter
import com.wheretopop.domain.popup.Popup
import com.wheretopop.domain.popup.PopupId
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.time.Instant

/**
 * 팝업(Popup) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "popups")
@EntityListeners(AuditingEntityListener::class)
data class PopupEntity(
    @Id
    @JdbcTypeCode(Types.BIGINT)
    @Convert(converter = PopupIdConverter::class)
    val id: PopupId,

    @Column(name = "building_id")
    @JdbcTypeCode(Types.BIGINT)
    val buildingId: Long?,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val address : String,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
    val deletedAt: Instant?
) {
    companion object {
        fun of(popup: Popup): PopupEntity {
            return PopupEntity(
                id = popup.id,
                name = popup.name,
                buildingId = popup.buildingId,
                address = popup.address,
                createdAt = popup.createdAt,
                deletedAt = popup.deletedAt
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
            deletedAt = deletedAt
        )
    }

    fun update(popup: Popup): PopupEntity {
        return PopupEntity(
            id = id,
            name = popup.name,
            buildingId = popup.buildingId,
            address = popup.address,
            createdAt = createdAt,
            deletedAt = deletedAt
        )
    }
}