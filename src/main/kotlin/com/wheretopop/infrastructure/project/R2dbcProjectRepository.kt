package com.wheretopop.infrastructure.project

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository

@Repository
internal class R2dbcProjectRepository(
    private val entityTemplate: R2dbcEntityTemplate
) : ProjectRepository {
    private val projectEntityClass = ProjectEntity::class.java

    override suspend fun findById(id: ProjectId): Project? {
        return entityTemplate
            .selectOne(query(where("id").`is`(id)), projectEntityClass)
            .awaitSingleOrNull()
            ?.toDomain()
    }

    override suspend fun findByOwnerId(ownerId: UserId): List<Project> {
        return entityTemplate
            .select(query(where("owner_id").`is`(ownerId)), projectEntityClass)
            .asFlow()
            .map { it.toDomain() }
            .toList()
    }

    override suspend fun save(project: Project): Project {
        val projectEntity = ProjectEntity.of(project)
        val exists = entityTemplate.exists(query(where("id").`is`(project.id)), projectEntityClass).awaitSingle()
        
        if (exists) {
            entityTemplate.update(projectEntity).awaitSingle()
        } else {
            entityTemplate.insert(projectEntity).awaitSingle()
        }
        
        return project
    }
} 