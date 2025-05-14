package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId

interface ProjectReader {
    suspend fun findById(id: ProjectId): Project?
    suspend fun findByOwnerId(ownerId: UserId): List<Project>
} 