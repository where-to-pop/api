package com.wheretopop.gateway.shared.enums

enum class PopulationDensity(
    val description: String,
    val min: Int,
    val max: Int
) {
    VERY_LOW("VERY_LOW", 0, 10_000),
    LOW("LOW", 10_001, 20_000),
    MEDIUM("MEDIUM", 20_001, 30_000),
    HIGH("HIGH", 30_001, 35_000),
    VERY_HIGH("VERY_HIGH", 35_001, Int.MAX_VALUE);

    companion object {
        fun from(value: Int): PopulationDensity =
            entries.firstOrNull { value in it.min..it.max }
                ?: throw IllegalArgumentException("인구밀도 값($value)에 해당하는 구간이 없습니다.")
    }
}