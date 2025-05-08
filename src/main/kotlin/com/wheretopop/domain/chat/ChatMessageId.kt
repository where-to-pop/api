package com.wheretopop.domain.chat

import com.wheretopop.shared.model.UniqueId

class ChatMessageId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): ChatMessageId {
            return ChatMessageId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): ChatMessageId {
            return ChatMessageId(UniqueId.of(value).value)
        }
    }
}
