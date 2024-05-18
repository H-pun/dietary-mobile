package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateEmailBodyRequest(
    val id: String,
    @SerialName("email")
    val emailAddress: String
)