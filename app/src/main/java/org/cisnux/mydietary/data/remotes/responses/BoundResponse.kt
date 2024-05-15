package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class BoundResponse(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)
