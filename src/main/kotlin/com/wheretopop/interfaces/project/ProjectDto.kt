package com.wheretopop.interfaces.project

import com.wheretopop.application.project.ProjectInput
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.project.ProjectInfo
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType

class ProjectDto {
    data class CreateRequest(
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
        fun toInput(ownerId: UserId) = ProjectInput.Create(
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
            additionalProjectInfo = additionalProjectInfo
        )
    }

    data class UpdateRequest(
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
        fun toInput(projectId: ProjectId) = ProjectInput.Update(
            id = projectId,
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
            additionalProjectInfo = additionalProjectInfo
        )
    }

    data class ProjectResponse(
        val id: Long,
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
        val additionalProjectInfo: String?,
        val ownerId: Long
    ) {
        companion object {
            fun from(info: ProjectInfo.Main): ProjectResponse {
                return ProjectResponse(
                    id = info.id.toLong(),
                    name = info.name,
                    brandName = info.brandName,
                    popupCategory = info.popupCategory,
                    popupType = info.popupType,
                    duration = info.duration,
                    primaryTargetAgeGroup = info.primaryTargetAgeGroup,
                    secondaryTargetAgeGroup = info.secondaryTargetAgeGroup,
                    brandScale = info.brandScale,
                    projectGoal = info.projectGoal,
                    additionalBrandInfo = info.additionalBrandInfo,
                    additionalProjectInfo = info.additionalProjectInfo,
                    ownerId = info.ownerId.toLong()
                )
            }
        }
    }
} 