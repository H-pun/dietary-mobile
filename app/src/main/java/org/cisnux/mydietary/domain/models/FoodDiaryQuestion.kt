package org.cisnux.mydietary.domain.models

data class FoodDiaryQuestion(
    val foodDiaryId: String,
    val foodQuestions: List<FoodQuestion>
)
