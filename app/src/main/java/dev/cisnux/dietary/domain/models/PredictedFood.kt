package dev.cisnux.dietary.domain.models

data class PredictedFood(
    val id: String,
    val name: String,
    val bound: Bound,
    val calories: Float,
    val fat: Float,
    val protein: Float,
    val carbohydrates: Float,
    val sugar: Float?,
    val questions: List<Question> = listOf(),
    val feedbacks: List<String> = listOf(),
)