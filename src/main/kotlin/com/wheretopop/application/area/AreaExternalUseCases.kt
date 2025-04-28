package com.wheretopop.application.area

interface AreaOpenDataUseCase {
    suspend fun callOpenDataApiAndSave()
}

interface AreaSnsUseCase {
    // suspend fun fetchSnsMentions(areaName: String): List<SnsMention>
}
