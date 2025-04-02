package com.wheretopop.shared.enums

enum class PopulationDensity(
    val description: String,
    val min: Int,
    val max: Int
) {
    VERY_LOW("0~10,000", 0, 10_000),
    LOW("10,001~20,000", 10_001, 20_000),
    MEDIUM("20,001~30,000", 20_001, 30_000),
    HIGH("30,001~35,000", 30_001, 35_000),
    VERY_HIGH("35,001~", 35_001, Int.MAX_VALUE);

    companion object {
        fun from(value: Int): PopulationDensity =
            entries.firstOrNull { value in it.min..it.max }
                ?: throw IllegalArgumentException("인구밀도 값($value)에 해당하는 구간이 없습니다.")
    }
}