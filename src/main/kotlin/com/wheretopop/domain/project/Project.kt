package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import java.time.Instant

class Project private constructor(
    val id: ProjectId = ProjectId.create(),
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
    val additionalProjectInfo: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {
    companion object {
        fun create(
            id: ProjectId = ProjectId.create(),
            ownerId: UserId,
            name: String,
            brandName: String,
            popupCategory: PopUpCategory,
            popupType: PopUpType,
            duration: String,
            primaryTargetAgeGroup: AgeGroup,
            secondaryTargetAgeGroup: AgeGroup?,
            brandScale: BrandScale,
            projectGoal: String,
            additionalBrandInfo: String?,
            additionalProjectInfo: String?,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ): Project {
            return Project(
                id,
                ownerId,
                name,
                brandName,
                popupCategory,
                popupType,
                duration,
                primaryTargetAgeGroup,
                secondaryTargetAgeGroup,
                brandScale,
                projectGoal,
                additionalBrandInfo,
                additionalProjectInfo,
                createdAt,
                updatedAt,
                deletedAt
            )
        }
    }
    
    /**
     * 프로젝트 정보를 업데이트한 새 Project 객체를 반환합니다.
     */
    fun update(
        name: String? = null,
        brandName: String? = null,
        popupCategory: PopUpCategory? = null,
        popupType: PopUpType? = null,
        duration: String? = null,
        primaryTargetAgeGroup: AgeGroup? = null,
        secondaryTargetAgeGroup: AgeGroup? = null,
        brandScale: BrandScale? = null,
        projectGoal: String? = null,
        additionalBrandInfo: String? = null,
        additionalProjectInfo: String? = null
    ): Project {
        return Project(
            id = id,
            ownerId = ownerId,
            name = name ?: this.name,
            brandName = brandName ?: this.brandName,
            popupCategory = popupCategory ?: this.popupCategory,
            popupType = popupType ?: this.popupType,
            duration = duration ?: this.duration,
            primaryTargetAgeGroup = primaryTargetAgeGroup ?: this.primaryTargetAgeGroup,
            secondaryTargetAgeGroup = secondaryTargetAgeGroup ?: this.secondaryTargetAgeGroup,
            brandScale = brandScale ?: this.brandScale,
            projectGoal = projectGoal ?: this.projectGoal,
            additionalBrandInfo = additionalBrandInfo ?: this.additionalBrandInfo,
            additionalProjectInfo = additionalProjectInfo ?: this.additionalProjectInfo,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt
        )
    }
} 