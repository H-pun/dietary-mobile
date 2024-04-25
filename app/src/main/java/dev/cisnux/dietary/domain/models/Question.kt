package dev.cisnux.dietary.domain.models

data class Question(
    val id: String,
    val question: String,
    val options: List<Option>,
)
