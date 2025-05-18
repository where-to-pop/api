package com.wheretopop.infrastructure.project

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId

internal interface ProjectRepository {
    fun findById(id: ProjectId): Project?
    fun findByOwnerId(ownerId: UserId): List<Project>
    fun save(project: Project): Project
} 