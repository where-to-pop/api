package com.wheretopop.infrastructure.chat

import com.wheretopop.domain.area.Area
import com.wheretopop.domain.area.AreaStore
import com.wheretopop.domain.chat.Chat
import com.wheretopop.domain.chat.ChatStore
import org.springframework.stereotype.Component

/**
 * AreaStore 인터페이스 구현체
 * 도메인 레이어와 인프라 레이어를 연결하는 역할을 담당
 */
@Component
class ChatStoreImpl(
    private val chatRepository: ChatRepository
) : ChatStore {

    override fun save(chat: Chat): Chat {
        return chatRepository.save(chat)
    }
    override fun save(chats: List<Chat>): List<Chat> {
        return chatRepository.save(chats)
    }
} 