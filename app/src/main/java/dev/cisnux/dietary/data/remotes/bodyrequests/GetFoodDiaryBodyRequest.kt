package dev.cisnux.dietary.data.remotes.bodyrequests

data class GetFoodDiaryBodyRequest(
    val userId: String,
    val date: String,
    val category: String,
    val query: String? = null
)
