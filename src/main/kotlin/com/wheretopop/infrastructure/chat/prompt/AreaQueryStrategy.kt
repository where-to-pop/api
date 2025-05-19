package com.wheretopop.infrastructure.chat.prompt

import com.wheretopop.interfaces.area.AreaToolRegistry
import mu.KotlinLogging
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.support.ToolCallbacks
import org.springframework.stereotype.Component

/**
 * 지역 정보 조회에 특화된 전략 구현체
 * 사용자가 지역 정보를 요청할 때 적절한 Tool Calling을 설정하여 응답합니다.
 */
@Component
class AreaQueryStrategy(
    private val areaToolRegistry: AreaToolRegistry
) : BaseChatPromptStrategy() {

    private val logger = KotlinLogging.logger {}
    /**
     * 전략 타입을 반환합니다.
     */
    override fun getType(): StrategyType {
        return StrategyType.AREA_QUERY
    }

    /**
     * 지역 정보 조회에 특화된 추가 프롬프트를 반환합니다.
     */
    override fun getAdditionalSystemPrompt(): String {
//        return SystemPrompt.AREA_QUERY_PROMPT
        return SystemPrompt.AREA_QUERY_PROMPT
    }
    
    /**
     * Tool Calling을 위한 채팅 옵션을 설정합니다.
     * 지역 관련 도구들을 등록하고 AI가 이를 사용할 수 있도록 합니다.
     */
    override fun getToolCallingChatOptions(): ToolCallingChatOptions {
        val toolCallbackChatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(*ToolCallbacks.from(areaToolRegistry))
            .internalToolExecutionEnabled(false)
            .temperature(0.2)
            .build()
        return toolCallbackChatOptions
    }

} 