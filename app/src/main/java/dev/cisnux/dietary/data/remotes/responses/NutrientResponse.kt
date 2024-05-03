package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class NutrientResponse(
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbohydrate: Float
)
