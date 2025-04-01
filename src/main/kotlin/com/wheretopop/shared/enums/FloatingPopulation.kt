package com.wheretopop.shared.enums

enum class FloatingPopulation(
    val description: String,
    val min: Int,
    val max: Int
) {
    VERY_LOW("VERY_LOW", 0, 10_000),
    LOW("LOW", 10_001, 20_000),
    MEDIUM("MEDIUM", 20_001, 30_000),
    HIGH("HIGH", 30_001, 40_000),
    VERY_HIGH("VERY_HIGH", 40_001, Int.MAX_VALUE);

    companion object {
        fun from(value: Int): FloatingPopulation =
            entries.firstOrNull { value in it.min..it.max }
                ?: throw IllegalArgumentException("유동인구 값($value)에 해당하는 구간이 없습니다.")
    }
}
