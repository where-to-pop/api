package com.wheretopop.shared.enums

enum class SnsMention(
    val description: String,
    val min: Int,
    val max: Int
) {
    VERY_LOW("100 이하", 0, 100),
    LOW("100~600", 100, 600),
    MEDIUM("600~2,000", 600, 2000),
    HIGH("2,000~5,000", 2000, 5000),
    VIRAL("5,000 이상", 5000, Int.MAX_VALUE)
}