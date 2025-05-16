package com.wheretopop.domain.project

import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.enums.AgeGroup
import com.wheretopop.shared.enums.BrandScale
import com.wheretopop.shared.enums.PopUpCategory
import com.wheretopop.shared.enums.PopUpType
import java.time.Instant

class ProjectInfo {
    data class Main(
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
        val deletedAt: Instant?
    )
} 