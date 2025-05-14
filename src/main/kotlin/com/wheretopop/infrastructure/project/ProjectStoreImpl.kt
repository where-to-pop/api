package com.wheretopop.infrastructure.project

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectStore
import org.springframework.stereotype.Component

@Component
internal class ProjectStoreImpl(
    private val projectRepository: ProjectRepository
) : ProjectStore {
    override suspend fun save(project: Project): Project {
        return projectRepository.save(project)
    }
} 