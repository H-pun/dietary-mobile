package dev.cisnux.dietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The body request to duplicate food diary
@Serializable
data class DuplicateFoodDiaryBodyRequest(
    // the id of food diary
    val id: String,
    // the category of food diary can be sarapan, makan siang or makan malam.
    val category: String,
    @SerialName(value = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)