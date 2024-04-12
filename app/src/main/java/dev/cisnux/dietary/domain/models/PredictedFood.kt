package dev.cisnux.dietary.domain.models

data class PredictedFood(
    val id: String,
    val name: String,
    val bound: Bound,
    val questions: List<Question> = listOf()
)