package com.wheretopop.application.project

import com.wheretopop.domain.project.ProjectCommand
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType

class ProjectInput {
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
        fun toCommand(): ProjectCommand.Create {
            return ProjectCommand.Create(
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
        fun toCommand(): ProjectCommand.Update {
            return ProjectCommand.Update(
                id = id,
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