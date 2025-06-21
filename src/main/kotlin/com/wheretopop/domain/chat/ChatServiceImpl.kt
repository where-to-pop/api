package com.wheretopop.domain.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wheretopop.domain.user.UserId
import com.wheretopop.infrastructure.chat.prompt.ExecutionPhase
import com.wheretopop.shared.enums.ChatMessageFinishReason
import com.wheretopop.shared.enums.ChatMessageRole
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.ErrorCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mu.KotlinLogging
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
    private val logger = KotlinLogging.logger {}

    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        // ISO 8601 형식으로 날짜 직렬화
        disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    
    // 진행 중인 ReAct 실행 스트림을 캐시 (Hot Stream)
    private val activeExecutions = ConcurrentHashMap<String, SharedFlow<String>>()
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
            stepResult = null,
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
        
        // 즉시 스트림 생성 및 캐시에 저장 (동기적으로)
        val executionKey = "${savedChat.id.value}_${System.currentTimeMillis()}"
        val mutableSharedFlow = MutableSharedFlow<String>(
            replay = 5,  // 최근 5개 메시지를 새로운 구독자에게 전송
            extraBufferCapacity = 1000
        )
        activeExecutions[executionKey] = mutableSharedFlow.asSharedFlow()
        
        // 백그라운드에서 AI 처리 시작  
        processMessageInBackground(savedChat, command.context, mutableSharedFlow, executionKey)
        
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
            stepResult = null,
            latencyMs = 0L,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            deletedAt = null
        ))
        
        // 사용자 메시지가 추가된 채팅 저장
        val savedChat = chatStore.save(messageAddedChat)
        
        // 즉시 Simple 정보 생성
        val simpleInfo = ChatInfoMapper.toSimpleInfo(savedChat)
        
        // 즉시 스트림 생성 및 캐시에 저장 (동기적으로)
        val executionKey = "${savedChat.id.value}_${System.currentTimeMillis()}" 
        val mutableSharedFlow = MutableSharedFlow<String>(
            replay = 5,  // 최근 5개 메시지를 새로운 구독자에게 전송
            extraBufferCapacity = 1000
        )
        activeExecutions[executionKey] = mutableSharedFlow.asSharedFlow()
        
        // 백그라운드에서 AI 처리 시작
        processMessageInBackground(savedChat, context, mutableSharedFlow, executionKey)
        
        return simpleInfo
    }

    /**
     * 특정 채팅의 ReAct 실행 상태를 스트림으로 조회합니다.
     */
    override fun getChatExecutionStatusStream(chatId: ChatId, userId: UserId, executionId: String?): SharedFlow<String> {
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
    private fun processMessageInBackground(
        chat: Chat,
        context: String?,
        mutableSharedFlow: MutableSharedFlow<String>,
        executionKey: String
    ) {
        // 백그라운드에서 실제 AI 처리 시작
        executionScope.launch {
            val chatMessageId = ChatMessageId.create()
            try {
                var finalCompleteResult: String? = null
                var stepResultAggregation: String? = null

                chatScenario.processUserMessageStream(chat, chatMessageId, context)
                    .map { reActStreamResponse ->
                        objectMapper.writeValueAsString(reActStreamResponse)
                    }
                    .collect { streamData ->
                        // 실시간으로 클라이언트들에게 데이터 전송
                        mutableSharedFlow.emit(streamData)
                        val responseData = objectMapper.readTree(streamData)
                        if (responseData.has("status")) {
                            val status = responseData.get("status");
                            if (status.has("phase")) {
                                val phaseString = status.get("phase").asText()
                                if (ExecutionPhase.valueOf(phaseString) == ExecutionPhase.STEP_COMPLETED && status.has("stepResult")) {
                                    val stepResult = status.get("stepResult").asText()
                                    logger.info { "stepResult: $stepResult" };
                                    // stepResultAggregation 이 null 이면 최초 값 할당, 그렇지 않으면 누적
                                    stepResultAggregation =
                                        stepResultAggregation?.plus(stepResult + "\n") ?: (stepResult + "\n")
                                }
                            }
                        }
                        // COMPLETED 단계에서 누적된 전체 결과 받기
                        if (responseData.has("isComplete") && responseData.get("isComplete").asBoolean()) {
                            finalCompleteResult = responseData.get("finalResult")?.asText()
                        }
                    }

                // 완료된 경우 전체 결과를 채팅에 저장
                finalCompleteResult?.let { result ->
                    val latestChat = chatReader.findById(chat.id) ?: return@launch
                    val updatedChat = latestChat.addMessage(ChatMessage.create(
                        id = chatMessageId,
                        chatId = chat.id,
                        role = ChatMessageRole.ASSISTANT,
                        content = result,
                        finishReason = null,
                        stepResult = stepResultAggregation,
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
                    id = chatMessageId,
                    chatId = chat.id,
                    role = ChatMessageRole.ASSISTANT,
                    content = errorMessage,
                    finishReason = ChatMessageFinishReason.ERROR,
                    latencyMs = 0L,
                    stepResult = null,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                    deletedAt = null
                ))
                chatStore.save(updatedChat)
            } finally {
                // 스트림 완료 및 캐시에서 제거
                mutableSharedFlow.tryEmit("closed")
                activeExecutions.remove(executionKey)
            }
        }
    }

}