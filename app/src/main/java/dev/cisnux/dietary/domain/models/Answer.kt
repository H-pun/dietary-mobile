package dev.cisnux.dietary.domain.models

import dev.cisnux.dietary.utils.QuestionType

data class Answer(
    val questionId: String,
    val question: String,
    val answer: String,
    val questionType: QuestionType
)
