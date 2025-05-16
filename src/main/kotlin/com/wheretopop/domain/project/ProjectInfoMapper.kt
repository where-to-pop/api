package com.wheretopop.domain.project

/**
 * Project 도메인 객체를 ProjectInfo DTO로 변환하는 매퍼 클래스
 * 이 클래스는 도메인 모델과 DTO 사이의 변환을 담당합니다.
 */
class ProjectInfoMapper {
    companion object {
        /**
         * Project 도메인 객체를 ProjectInfo.Main으로 변환
         */
        fun toMainInfo(project: Project): ProjectInfo.Main {
            return ProjectInfo.Main(
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
} 