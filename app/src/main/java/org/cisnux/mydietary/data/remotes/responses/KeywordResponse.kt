package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class KeywordResponse(
    val id: String,
    val title: String,
)
