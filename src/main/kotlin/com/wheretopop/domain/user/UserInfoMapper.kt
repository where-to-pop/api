package com.wheretopop.domain.user

/**
 * User 도메인 객체를 UserInfo DTO로 변환하는 매퍼 클래스
 * 이 클래스는 도메인 모델과 DTO 사이의 변환을 담당합니다.
 */
class UserInfoMapper {
    companion object {
        /**
         * User 도메인 객체를 UserInfo.Main으로 변환
         */
        fun toMainInfo(user: User): UserInfo.Main {
            return UserInfo.Main(
                id = user.id,
                username = user.username,
                email = user.email,
                profileImageUrl = user.profileImageUrl,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                deletedAt = user.deletedAt
            )
        }
    }
} 