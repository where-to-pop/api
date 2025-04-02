package com.wheretopop.shared.enums

enum class BuildingSize(
    val description: String,
    val min: Int,
    val max: Int,
) {
    SMALL("100 이하", 0, 100),
    MEDIUM("100~500", 100, 500),
    LARGE("500 이상", 500, Int.MAX_VALUE);

    companion object {
        fun from(value: String): BuildingSize =
            values().firstOrNull { it.name == value }
                ?: throw IllegalArgumentException("BuildingSize 값($value)에 해당하는 구간이 없습니다.")
    }
}
