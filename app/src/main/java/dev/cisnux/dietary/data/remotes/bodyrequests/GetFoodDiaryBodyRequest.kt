package dev.cisnux.dietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

@Serializable
data class GetFoodDiaryBodyRequest(
    val days: Long,
    val category: String,
    val query: String? = null
)
