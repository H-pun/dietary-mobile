package org.cisnux.mydietary.domain.models

data class Question(
    val id: String,
    val question: String,
    val options: List<Option>,
)
