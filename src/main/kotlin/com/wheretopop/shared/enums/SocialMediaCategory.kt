package com.wheretopop.shared.enums

/**
 * 소셜 미디어 카테고리 종류
 */
enum class SocialMediaCategory {
    SNS,        // 소셜 네트워크 서비스 (Facebook, Instagram, Twitter 등)
    BLOG,       // 블로그 형태 (네이버 블로그, 티스토리 등)
    NEWS,       // 뉴스/미디어 형태 (언론사, 포털 뉴스 등)
    COMMUNITY,  // 커뮤니티 형태 (카페, 포럼 등)
    REVIEW,     // 리뷰 서비스 (맛집 리뷰, 장소 리뷰 등)
    OTHER;      // 기타 분류되지 않는 형태
    
    companion object {
        fun fromString(value: String?): SocialMediaCategory {
            return when(value?.uppercase()) {
                "SNS" -> SNS
                "BLOG" -> BLOG
                "NEWS" -> NEWS
                "COMMUNITY" -> COMMUNITY
                "REVIEW" -> REVIEW
                else -> OTHER
            }
        }
    }
} 