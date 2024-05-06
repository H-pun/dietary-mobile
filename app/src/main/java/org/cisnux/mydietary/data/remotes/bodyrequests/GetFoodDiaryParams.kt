package org.cisnux.mydietary.data.remotes.bodyrequests

data class GetFoodDiaryParams(
    val userId: String,
    val date: String? = null,
    val category: String? = null,
    val query: String? = null
)
