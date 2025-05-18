package com.wheretopop.domain.chat

import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId

/**
 * 채팅 정보 조회를 위한 도메인 서비스 인터페이스
 */
interface ChatReader {
    /**
     * 모든 채팅을 조회합니다.
     */
    fun findAll(): List<Chat>
    
    /**
     * ID로 채팅을 조회합니다.
     */
    fun findById(id: ChatId): Chat?
    
    /**
     * 사용자 ID로 채팅 목록을 조회합니다.
     */
    fun findByUserId(userId: UserId): List<Chat>
    
    /**
     * 프로젝트 ID로 채팅 목록을 조회합니다.
     */
    fun findByProjectId(projectId: ProjectId): List<Chat>
} 