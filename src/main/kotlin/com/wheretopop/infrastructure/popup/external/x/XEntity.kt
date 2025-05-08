package com.wheretopop.infrastructure.popup.external.x

import com.wheretopop.domain.popup.PopupId
import com.wheretopop.shared.model.UniqueId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

class XId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): XId {
            return XId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): XId {
            return XId(UniqueId.of(value).value)
        }
    }
}

@Table("popup_x")
data class XEntity(
    @Id
    @Column("id")
    val id: XId = XId.create(),

    @Column("popup_id")
    val popupId: PopupId,

    @Column("written_at")
    val writtenAt: Instant,

    @Column("content")
    val content: String,

    @Column("emotion_score")
    val emotionScore: EmotionScore,

    @Column("created_at")
    val createdAt: Instant = Instant.now()
) {
    companion object {
        fun of(xResponse: XResponse, popupId: PopupId): XEntity {
            return XEntity(
                id = XId.create(),
                popupId = popupId,
                writtenAt = xResponse.writtenAt,
                content = xResponse.content,
                emotionScore = xResponse.emotionScore,
                createdAt = Instant.now()
            )
        }
    }
}

@WritingConverter
class XIdToLongConverter : Converter<XId, Long> {
    override fun convert(source: XId) = source.toLong()
}

@ReadingConverter
class LongToXIdConverter : Converter<Long, XId> {
    override fun convert(source: Long) = XId.of(source)
}