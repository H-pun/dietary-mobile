package dev.cisnux.dietary.data.remotes.bodyrequests

data class GetFoodDiaryParams(
    val userId: String,
    val date: String,
    val category: String,
    val query: String? = null
)
