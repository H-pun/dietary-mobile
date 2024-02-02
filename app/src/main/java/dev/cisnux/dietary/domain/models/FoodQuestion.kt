package dev.cisnux.dietary.domain.models

data class FoodQuestion(
    val foodId: String,
    val foodName: String,
    val answers: List<Answer>
)
