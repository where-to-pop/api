package com.wheretopop.infrastructure.project

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.project.ProjectReader
import com.wheretopop.domain.user.UserId
import org.springframework.stereotype.Component

@Component
internal class ProjectReaderImpl(
    private val projectRepository: ProjectRepository
) : ProjectReader {
    override fun findById(id: ProjectId): Project? {
        return projectRepository.findById(id)
    }

    override fun findByOwnerId(ownerId: UserId): List<Project> {
        return projectRepository.findByOwnerId(ownerId)
    }
} 