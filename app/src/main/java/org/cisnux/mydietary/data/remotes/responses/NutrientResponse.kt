package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class NutrientResponse(
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbohydrate: Float
)
