package com.wheretopop.shared.enums

enum class BrandSize(
    val description: String,
) {
    SMALL("소규모(중소)"),
    MEDIUM("중견"),
    LARGE("대기업");

    companion object {
        fun from(value: String): BrandSize =
            values().firstOrNull { it.name == value }
                ?: throw IllegalArgumentException("BrandSize 값($value)에 해당하는 구간이 없습니다.")
    }
}