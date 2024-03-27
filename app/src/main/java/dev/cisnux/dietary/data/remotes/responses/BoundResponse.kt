package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class BoundResponse(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)
