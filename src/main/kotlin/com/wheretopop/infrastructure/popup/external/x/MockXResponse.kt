package com.wheretopop.infrastructure.popup.external.x

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val mockXResponses = listOf(
    XResponse(formatDateToInstant("2024.10.28"), "팝업스토어 다녀왔는데 분위기 미쳤다!", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.09.01"), "기대했던 것보단 별로였던 팝업스토어...", EmotionScore.BAD),
    XResponse(formatDateToInstant("2025.02.02"), "팝업스토어 굿즈 퀄리티 대박!", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.10.02"), "줄 너무 길어서 그냥 돌아옴...", EmotionScore.VERY_BAD),
    XResponse(formatDateToInstant("2024.03.28"), "인스타 감성 뿜뿜한 팝업스토어 💕", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2025.02.05"), "한 번쯤은 가볼만한 듯", EmotionScore.NEUTRAL),
    XResponse(formatDateToInstant("2024.06.02"), "생각보다 볼 게 없었음", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.08.16"), "직원들 친절해서 기분 좋았음", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.09.23"), "너무 정신없고 사람 많음... 피곤함", EmotionScore.VERY_BAD),
    XResponse(formatDateToInstant("2025.04.14"), "컨셉 너무 예쁘고 감각 있었음", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.07.16"), "팝업스토어 사진 맛집📸", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.10.17"), "이게 그 유명한 팝업...? 기대 이하", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.12.12"), "팝업에서 산 제품 퀄리티 대박", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.11.01"), "이벤트 참여하고 굿즈도 받았음 😎", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2025.02.11"), "주차도 힘들고 길 안내도 엉망", EmotionScore.VERY_BAD),
    XResponse(formatDateToInstant("2024.01.13"), "나쁘지 않았지만 다시 가고 싶진 않음", EmotionScore.NEUTRAL),
    XResponse(formatDateToInstant("2024.05.09"), "팝업 분위기 최고였음!!", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.08.05"), "사람들 매너 좀 챙겼으면...", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.07.30"), "브랜드 감성 제대로 느끼고 옴", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.12.01"), "비 오는 날이라 더 감성적이었음 ☔", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.11.18"), "사진 찍기 좋고 조명도 예쁨", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.04.12"), "굿즈 수량 부족해서 못 삼 ㅠㅠ", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.02.19"), "팝업스토어 너무 예뻐서 놀람", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.01.18"), "그냥 시간 때우기용", EmotionScore.NEUTRAL),
    XResponse(formatDateToInstant("2024.06.16"), "매장 구성은 좋았는데 안내가 아쉬웠음", EmotionScore.NEUTRAL),
    XResponse(formatDateToInstant("2024.05.17"), "브랜드 세계관 제대로 보여준 팝업!", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.09.23"), "인스타에서 본 게 다였음", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.09.26"), "굿즈 퀄리티가 예상보다 별로", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.08.28"), "데이트 코스로 추천!", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.10.23"), "줄 서는데 2시간은 에바...", EmotionScore.VERY_BAD),
    XResponse(formatDateToInstant("2024.10.05"), "브랜드 팬이면 무조건 가야함", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.11.23"), "신선한 경험이었음", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2025.01.22"), "진짜 감성 끝판왕", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.04.26"), "팝업보다 사람 구경한 느낌", EmotionScore.NEUTRAL),
    XResponse(formatDateToInstant("2024.03.19"), "너무 좁고 불편했음", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.02.14"), "구성 알찼고 설명도 친절했음", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.08.12"), "사진만 찍고 바로 나옴", EmotionScore.NEUTRAL),
    XResponse(formatDateToInstant("2024.09.09"), "세세한 디테일까지 신경쓴 느낌", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.06.03"), "그럭저럭 평범한 팝업", EmotionScore.NEUTRAL),
    XResponse(formatDateToInstant("2025.02.12"), "다신 안 갈 듯", EmotionScore.VERY_BAD),
    XResponse(formatDateToInstant("2025.03.11"), "팝업스토어 후기 봤을 땐 괜찮았는데...", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.04.25"), "굿즈 질도 좋고 포장도 예쁨", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.10.29"), "사람이 너무 많아서 정신 없었음", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.05.30"), "와 진짜 여태 본 팝업 중 최고", EmotionScore.VERY_GOOD),
    XResponse(formatDateToInstant("2024.12.13"), "팝업스토어 알차게 잘 구성되어 있었음", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.07.06"), "시간이 너무 아까웠음", EmotionScore.VERY_BAD),
    XResponse(formatDateToInstant("2024.07.02"), "굿즈 품절 너무 빠르다;;", EmotionScore.BAD),
    XResponse(formatDateToInstant("2024.02.12"), "브랜드 감성 덕질하기 딱 좋음", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.08.15"), "콘셉트 좋아서 사진도 잘 나왔음", EmotionScore.GOOD),
    XResponse(formatDateToInstant("2024.03.14"), "친구랑 재밌게 다녀옴!", EmotionScore.GOOD)
)


fun formatDateToInstant(dateString: String, pattern: String = "yyyy.MM.dd"): Instant {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val localDate = LocalDate.parse(dateString, formatter)

    return localDate.atStartOfDay().toInstant(ZoneOffset.UTC)
}