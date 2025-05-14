package com.wheretopop.infrastructure.project

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId

internal interface ProjectRepository {
    suspend fun findById(id: ProjectId): Project?
    suspend fun findByOwnerId(ownerId: UserId): List<Project>
    suspend fun save(project: Project): Project
} 