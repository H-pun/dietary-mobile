package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The body request to add a new password
@Serializable
data class NewPasswordBodyRequest(
    val code: String,
    @SerialName(value = "email")
    val emailAddress: String,
    @SerialName(value = "password")
    val newPassword: String
)
