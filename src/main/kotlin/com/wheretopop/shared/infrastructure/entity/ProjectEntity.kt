package com.wheretopop.shared.infrastructure.entity

import com.wheretopop.domain.project.Project
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.time.Instant

/**
 * 프로젝트(Project) 테이블 엔티티
 * JPA 기반으로 구현
 */
@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener::class)
class ProjectEntity(
    @Id
    @JdbcTypeCode(Types.BIGINT)
    val id: Long,
    
    @Column(name = "owner_id", nullable = false)
    val ownerId: Long,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(name = "brand_name", nullable = false)
    val brandName: String,
    
    @Column(name = "popup_category", nullable = false)
    @Enumerated(EnumType.STRING)
    val popupCategory: PopUpCategory,
    
    @Column(name = "popup_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val popupType: PopUpType,
    
    @Column(nullable = false)
    val duration: String,
    
    @Column(name = "primary_target_age_group", nullable = false)
    @Enumerated(EnumType.STRING)
    val primaryTargetAgeGroup: AgeGroup,
    
    @Column(name = "secondary_target_age_group")
    @Enumerated(EnumType.STRING)
    val secondaryTargetAgeGroup: AgeGroup?,
    
    @Column(name = "brand_scale", nullable = false)
    @Enumerated(EnumType.STRING)
    val brandScale: BrandScale,
    
    @Column(name = "project_goal", nullable = false)
    val projectGoal: String,
    
    @Column(name = "additional_brand_info")
    val additionalBrandInfo: String?,
    
    @Column(name = "additional_project_info")
    val additionalProjectInfo: String?,
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
    
    @Column(name = "deleted_at")
    val deletedAt: Instant? = null
) {
    companion object {
        fun of(project: Project): ProjectEntity {
            return ProjectEntity(
                id = project.id.toLong(),
                ownerId = project.ownerId.toLong(),
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
            id = ProjectId.of(id),
            ownerId = UserId.of(ownerId),
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
