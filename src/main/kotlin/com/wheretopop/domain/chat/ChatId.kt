package com.wheretopop.domain.chat

import com.wheretopop.shared.model.UniqueId

class ChatId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): ChatId {
            return ChatId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): ChatId {
            return ChatId(UniqueId.of(value).value)
        }
    }
}
