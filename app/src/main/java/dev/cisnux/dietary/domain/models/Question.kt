package dev.cisnux.dietary.domain.models

import dev.cisnux.dietary.utils.QuestionType

data class Question(
    val question: String,
    val type: QuestionType
)
