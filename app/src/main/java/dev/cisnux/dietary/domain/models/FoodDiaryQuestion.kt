package dev.cisnux.dietary.domain.models

data class FoodDiaryQuestion(
    val foodDiaryId: String,
    val foodQuestions: List<FoodQuestion>
)
