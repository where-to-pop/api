package com.wheretopop.shared.enums

/**
 * 소셜 미디어 플랫폼 종류
 */
enum class SocialMediaType {
    INSTAGRAM,
    FACEBOOK,
    TWITTER,
    TIKTOK,
    BLOG,
    NEWS,
    OTHER;
    
    companion object {
        fun fromString(value: String?): SocialMediaType {
            return when(value?.uppercase()) {
                "INSTAGRAM" -> INSTAGRAM
                "FACEBOOK" -> FACEBOOK
                "TWITTER" -> TWITTER
                "TIKTOK" -> TIKTOK
                "BLOG" -> BLOG
                "NEWS" -> NEWS
                else -> OTHER
            }
        }
    }
} 