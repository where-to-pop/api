package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import java.time.Instant

class Project private constructor(
    val id: ProjectId,
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
            id: ProjectId,
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
} 