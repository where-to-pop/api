package com.wheretopop.infrastructure.chat

import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions


interface ChatAssistant {
    /**
     * 대화 ID를 사용하여 프롬프트를 처리하고 응답을 생성합니다.
     * 대화 이력은 ChatMemory에 저장되고 다음 호출 시 자동으로 활용됩니다.
     *
     * @param conversationId 대화 식별자
     * @param prompt 사용자 프롬프트
     * @param toolCallingChatOption 도구 호출 옵션
     * @return AI 모델의 응답
     */
    fun call(conversationId: String, prompt: Prompt, toolCallingChatOption: ToolCallingChatOptions?): ChatResponse
}
