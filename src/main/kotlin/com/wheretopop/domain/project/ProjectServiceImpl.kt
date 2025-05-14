package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(
    private val projectReader: ProjectReader,
    private val projectStore: ProjectStore
) : ProjectService {

    override suspend fun createProject(command: ProjectCommand.Create): ProjectInfo.Main {
        val project = command.toDomain()
        val savedProject = projectStore.save(project)
        return ProjectInfoMapper.toMainInfo(savedProject)
    }

    override suspend fun updateProject(command: ProjectCommand.Update): ProjectInfo.Main {
        val existingProject = projectReader.findById(command.id)
            ?: throw IllegalArgumentException("프로젝트를 찾을 수 없습니다: ${command.id}")

        val updatedProject = command.toDomain(existingProject)
        val savedProject = projectStore.save(updatedProject)
        return ProjectInfoMapper.toMainInfo(savedProject)
    }

    override suspend fun findProjectById(id: ProjectId): ProjectInfo.Main? {
        val project = projectReader.findById(id) ?: return null
        return ProjectInfoMapper.toMainInfo(project)
    }

    override suspend fun findProjectsByOwnerId(ownerId: UserId): List<ProjectInfo.Main> {
        val projects = projectReader.findByOwnerId(ownerId)
        return projects.map { ProjectInfoMapper.toMainInfo(it) }
    }
} 