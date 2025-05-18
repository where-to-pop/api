package com.wheretopop.infrastructure.project

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.infrastructure.entity.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA 프로젝트 저장소 인터페이스
 */
@Repository
interface JpaProjectRepository : JpaRepository<ProjectEntity, ProjectId> {
    fun findByOwnerId(ownerId: UserId): List<ProjectEntity>
}

/**
 * 프로젝트 저장소 JPA 구현체
 */
@Repository
class JpaProjectRepositoryImpl(
    private val jpaRepository: JpaProjectRepository
) : ProjectRepository {

    override fun findById(id: ProjectId): Project? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByOwnerId(ownerId: UserId): List<Project> {
        return jpaRepository.findByOwnerId(ownerId).map { it.toDomain() }
    }

    override fun save(project: Project): Project {
        val projectEntity = ProjectEntity.of(project)
        val savedEntity = jpaRepository.save(projectEntity)
        return savedEntity.toDomain()
    }
} 