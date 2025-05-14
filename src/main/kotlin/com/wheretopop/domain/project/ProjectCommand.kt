package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import java.time.Instant

class ProjectCommand {
    data class Create(
        val ownerId: UserId,
        val name: String,
        val brandName: String,
        val popupCategory: PopUpCategory,
        val popupType: PopUpType,
        val duration: String,
        val primaryTargetAgeGroup: AgeGroup,
        val secondaryTargetAgeGroup: AgeGroup?,
        val brandScale: BrandScale,
        val projectGoal: String,
        val additionalBrandInfo: String?,
        val additionalProjectInfo: String?
    ) {
        fun toDomain(): Project {
            return Project.create(
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
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                deletedAt = null
            )
        }
    }

    data class Update(
        val id: ProjectId,
        val name: String?,
        val brandName: String?,
        val popupCategory: PopUpCategory?,
        val popupType: PopUpType?,
        val duration: String?,
        val primaryTargetAgeGroup: AgeGroup?,
        val secondaryTargetAgeGroup: AgeGroup?,
        val brandScale: BrandScale?,
        val projectGoal: String?,
        val additionalBrandInfo: String?,
        val additionalProjectInfo: String?
    ) {
        fun toDomain(existingProject: Project): Project {
            return existingProject.update(
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
            )
        }
    }
} 