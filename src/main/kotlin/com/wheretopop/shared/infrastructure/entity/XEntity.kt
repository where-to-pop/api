package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.infrastructure.popup.external.x.EmotionScore
import com.wheretopop.infrastructure.popup.external.x.XResponse
import com.wheretopop.shared.domain.identifier.XId
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.time.Instant


/**
 * X 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "popup_x")
@EntityListeners(AuditingEntityListener::class)
class XEntity(
    @Id
    val id: Long,

    @Column(name = "popup_id", nullable = false)
    val popupId: Long,

    @Column(name = "written_at", nullable = false)
    val writtenAt: Instant,

    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "emotion_score", nullable = false)
    @Enumerated(EnumType.STRING)
    val emotionScore: EmotionScore,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
) {
    companion object {
        fun of(xResponse: XResponse, popupId: PopupId): XEntity {
            return XEntity(
                id = XId.create().toLong(),
                popupId = popupId.toLong(),
                writtenAt = xResponse.writtenAt,
                content = xResponse.content,
                emotionScore = xResponse.emotionScore
            )
        }
    }
}