package com.wheretopop.infrastructure.project

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("projects")
internal class ProjectEntity @PersistenceCreator private constructor(
    @Id
    @Column("id")
    val id: ProjectId,
    @Column("owner_id")
    val ownerId: UserId,
    @Column("name")
    val name: String,
    @Column("brand_name")
    val brandName: String,
    @Column("popup_category")
    val popupCategory: PopUpCategory,
    @Column("popup_type")
    val popupType: PopUpType,
    @Column("duration")
    val duration: String,
    @Column("primary_target_age_group")
    val primaryTargetAgeGroup: AgeGroup,
    @Column("secondary_target_age_group")
    val secondaryTargetAgeGroup: AgeGroup?,
    @Column("brand_scale")
    val brandScale: BrandScale,
    @Column("project_goal")
    val projectGoal: String,
    @Column("additional_brand_info")
    val additionalBrandInfo: String?,
    @Column("additional_project_info")
    val additionalProjectInfo: String?,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant?
) {
    companion object {
        fun of(project: Project): ProjectEntity {
            return ProjectEntity(
                id = project.id,
                ownerId = project.ownerId,
                name = project.name,
                brandName = project.brandName,
                popupCategory = project.popupCategory,
                popupType = project.popupType,
                duration = project.duration,
                primaryTargetAgeGroup = project.primaryTargetAgeGroup,
                secondaryTargetAgeGroup = project.secondaryTargetAgeGroup,
                brandScale = project.brandScale,
                projectGoal = project.projectGoal,
                additionalBrandInfo = project.additionalBrandInfo,
                additionalProjectInfo = project.additionalProjectInfo,
                createdAt = project.createdAt,
                updatedAt = project.updatedAt,
                deletedAt = project.deletedAt
            )
        }
    }

    fun toDomain(): Project {
        return Project.create(
            id = id,
            ownerId = ownerId,
            name = name,
            brandName = brandName,
            popupCategory = popupCategory,
            popupType = popupType,
            duration = duration,
            primaryTargetAgeGroup = primaryTargetAgeGroup,
            secondaryTargetAgeGroup = secondaryTargetAgeGroup,
            brandScale = brandScale,
            projectGoal = projectGoal,
            additionalBrandInfo = additionalBrandInfo,
            additionalProjectInfo = additionalProjectInfo,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }

    fun update(project: Project): ProjectEntity {
        return ProjectEntity(
            id = id,
            ownerId = ownerId,
            name = project.name,
            brandName = project.brandName,
            popupCategory = project.popupCategory,
            popupType = project.popupType,
            duration = project.duration,
            primaryTargetAgeGroup = project.primaryTargetAgeGroup,
            secondaryTargetAgeGroup = project.secondaryTargetAgeGroup,
            brandScale = project.brandScale,
            projectGoal = project.projectGoal,
            additionalBrandInfo = project.additionalBrandInfo,
            additionalProjectInfo = project.additionalProjectInfo,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
}

@WritingConverter
class ProjectIdToLongConverter : Converter<ProjectId, Long> {
    override fun convert(source: ProjectId) = source.toLong()
}

@ReadingConverter
class LongToProjectIdConverter : Converter<Long, ProjectId> {
    override fun convert(source: Long) = ProjectId.of(source)
}
