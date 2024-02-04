package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse<out T>(
    val status: String,
    val message: String?,
    val data: T
)
