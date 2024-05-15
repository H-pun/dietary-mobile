package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class OptionResponse(
    val id: String,
    val answer: String,
    val reference: String?
)
