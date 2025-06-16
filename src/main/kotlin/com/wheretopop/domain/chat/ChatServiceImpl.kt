package com.wheretopop.domain.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.ChatMessageRole
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * ChatService 인터페이스 구현체
 * 도메인 서비스와 인프라를 조율하여 애플리케이션 기능을 제공합니다.
 */
@Service
class ChatServiceImpl(
    private val chatReader: ChatReader,
    private val chatStore: ChatStore,
    private val chatScenario: ChatScenario
): ChatService {
    
    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        // ISO 8601 형식으로 날짜 직렬화
        disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    
    // 진행 중인 ReAct 실행 스트림을 캐시 (Hot Stream)
    private val activeExecutions = ConcurrentHashMap<String, Flow<String>>()
    private val executionScope = CoroutineScope(Dispatchers.IO)

    /**
     * 새 채팅을 초기화합니다. (스트림 기반)
     */
    override fun initializeChat(command: ChatCommand.InitializeChat): ChatInfo.Detail {
        val chat = command.toDomain()
        val messageAddedChat = chat.addMessage(ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.USER,
            content = command.initialMessage,
            finishReason = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        ))
        
        // 제목 생성
        val title = chatScenario.generateTitle(messageAddedChat)
        val chatWithTitle = messageAddedChat.update(title = title)
        
        // 즉시 제목과 사용자 메시지가 포함된 채팅 저장
        val savedChat = chatStore.save(chatWithTitle)
        
        // 백그라운드에서 스트림 방식으로 AI 응답 처리
        processMessageInBackground(savedChat, command.context)
        
        return ChatInfoMapper.toDetailInfo(savedChat)
    }

    /**
     * 채팅 정보를 업데이트합니다.
     */
    override fun updateChat(command: ChatCommand.UpdateChat): ChatInfo.Main {
        val (chatId, userId, title, isActive, ) = command
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        if (chat.userId != userId) {
            throw ErrorCode.COMMON_FORBIDDEN.toException()
        }
        val updatedChat = chat.update(title, isActive)
        val savedChat = chatStore.save(updatedChat)
        return ChatInfoMapper.toMainInfo(savedChat)
    }

    /**
     * 채팅을 삭제(소프트 삭제)합니다.
     */
    override fun deleteChat(command: ChatCommand.DeleteChat): ChatInfo.Main {
        val (chatId) = command
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        
        val deletedChat = chat.delete()
        val savedChat = chatStore.save(deletedChat)
        
        return ChatInfoMapper.toMainInfo(savedChat)
    }
    
    /**
     * 채팅에 사용자 메시지를 추가하고 백그라운드에서 ReAct 실행을 시작합니다.
     * 즉시 Simple 정보를 반환하고, 실행 상태는 getChatExecutionStatusStream으로 모니터링 가능합니다.
     */
    override fun sendMessage(chatId: ChatId, message: String, context: String?): ChatInfo.Simple {
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        
        // 사용자 메시지를 채팅에 추가
        val messageAddedChat = chat.addMessage(ChatMessage.create(
            chatId = chat.id,
            role = ChatMessageRole.USER,
            content = message,
            finishReason = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        ))
        
        // 사용자 메시지가 추가된 채팅 저장
        val savedChat = chatStore.save(messageAddedChat)
        
        // 즉시 Simple 정보 생성
        val simpleInfo = ChatInfoMapper.toSimpleInfo(savedChat)
        
        // 백그라운드에서 스트림 방식으로 AI 응답 처리
        processMessageInBackground(savedChat, context)
        
        return simpleInfo
    }

    /**
     * 특정 채팅의 ReAct 실행 상태를 스트림으로 조회합니다.
     */
    override fun getChatExecutionStatusStream(chatId: ChatId, userId: UserId, executionId: String?): Flow<String> {
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        
        // 해당 채팅의 활성 실행을 찾기
        val chatKey = chatId.value.toString()
        val activeExecution = activeExecutions.entries.find { (key, _) -> 
            key.startsWith(chatKey) 
        }?.value
        
        return activeExecution ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
    }
    
    /**
     * 채팅의 상세 정보를 조회합니다.
     */
    override fun getDetail(chatId: ChatId): ChatInfo.Detail {
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        return ChatInfoMapper.toDetailInfo(chat)
    }
    
    /**
     * 채팅의 기본 정보 목록을 조회합니다.
     */
    override fun getList(userId: UserId): List<ChatInfo.Main> {
        val chats = chatReader.findByUserId(userId)
        return chats.map { ChatInfoMapper.toMainInfo(it) }
    }

    override fun getSimple(chatId: ChatId): ChatInfo.Simple {
        val chat = chatReader.findById(chatId) ?: throw ErrorCode.COMMON_ENTITY_NOT_FOUND.toException()
        return ChatInfoMapper.toSimpleInfo(chat);
    }
    
    /**
     * 백그라운드에서 메시지를 처리하고 결과를 저장합니다.
     */
    private fun processMessageInBackground(chat: Chat, context: String?) {
        val executionKey = "${chat.id.value}_${System.currentTimeMillis()}"
        
        // chatScenario를 직접 호출해서 JSON 변환 후 Hot Stream으로 변환
        val executionFlow = chatScenario.processUserMessageStream(chat, context)
            .map { reActStreamResponse ->
                objectMapper.writeValueAsString(reActStreamResponse)
            }
            .shareIn(
                scope = executionScope,
                started = SharingStarted.Lazily, // 첫 번째 collector가 연결될 때 시작
                replay = 1 // 마지막 값을 새로운 collector에게 전달
            )
            .onCompletion { 
                // 실행 완료 시 캐시에서 제거
                activeExecutions.remove(executionKey)
            }
        
        // Hot Stream을 캐시에 저장
        activeExecutions[executionKey] = executionFlow
        
        // 백그라운드에서 실행 시작 (결과는 저장) - Hot Stream이므로 한 번만 실행됨
        executionScope.launch {
            try {
                var finalCompleteResult: String? = null
                
                executionFlow.collect { streamData ->
                    val responseData = objectMapper.readTree(streamData)
                    
                    // COMPLETED 단계에서 누적된 전체 결과 받기
                    if (responseData.has("isComplete") && responseData.get("isComplete").asBoolean()) {
                        finalCompleteResult = responseData.get("finalResult")?.asText()
                    }
                }
                
                // 완료된 경우 전체 결과를 채팅에 저장
                finalCompleteResult?.let { result ->
                    val latestChat = chatReader.findById(chat.id) ?: return@launch
                    val updatedChat = latestChat.addMessage(ChatMessage.create(
                        chatId = chat.id,
                        role = ChatMessageRole.ASSISTANT,
                        content = result,
                        finishReason = null,
                        latencyMs = 0L,
                        createdAt = Instant.now(),
                        updatedAt = Instant.now(),
                        deletedAt = null
                    ))
                    chatStore.save(updatedChat)
                }
            } catch (e: Exception) {
                // 실행 실패 시 사용자 친화적인 에러 메시지를 채팅에 저장
                val errorMessage = "죄송해요, 일시적인 문제가 발생했어요. 다시 시도해 주세요."
                val latestChat = chatReader.findById(chat.id) ?: return@launch
                val updatedChat = latestChat.addMessage(ChatMessage.create(
                    chatId = chat.id,
                    role = ChatMessageRole.ASSISTANT,
                    content = errorMessage,
                    finishReason = null,
                    latencyMs = 0L,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                    deletedAt = null
                ))
                chatStore.save(updatedChat)
            }
        }
    }
}