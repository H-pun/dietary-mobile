package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class DietProgressResponse(
    val weight: Float,
    val waistCircumference: Float,
    val updatedAt: String
)
