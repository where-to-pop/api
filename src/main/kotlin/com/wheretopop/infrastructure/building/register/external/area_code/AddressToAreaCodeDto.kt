package com.wheretopop.infrastructure.building.register.external.area_code

data class AddressToAreaCodeResponse(
    val response: ResponseBody
)

data class ResponseBody(
    val result: Result
)

data class Result(
    val items: List<ResultItem>
)

data class ResultItem(
    val id: String
)

data class AreaCode(
    val sigunguCd: String,
    val bjdongCd: String,
    val bun: String,
    val ji: String,
)