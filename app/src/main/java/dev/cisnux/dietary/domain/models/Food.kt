package dev.cisnux.dietary.domain.models

data class Food(
    val id: String,
    val name: String,
    val calories: Float,
    val fat: Float,
    val protein: Float,
    val carbohydrates: Float,
    val sugar: Float?,
    val questions: List<Question> = listOf(),
    val feedback: List<String> = listOf(),
)