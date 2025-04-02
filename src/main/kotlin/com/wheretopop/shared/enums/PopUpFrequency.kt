package com.wheretopop.shared.enums

enum class PopUpFrequency(
    val description: String,
    val min: Int,
    val max: Int
) {
    RARE("0~2회", 0, 2),
    OCCASIONAL("3~5회", 3, 5),
    FREQUENT("6~10회", 6, 10),
    VERY_FREQUENT("10회 이상", 10, Int.MAX_VALUE);

    companion object {
        fun from(value: Int): PopUpFrequency =
            entries.firstOrNull { value in it.min..it.max }
                ?: throw IllegalArgumentException("팝업 빈도 값($value)에 해당하는 구간이 없습니다.")
    }
}