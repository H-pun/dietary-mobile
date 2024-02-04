package dev.cisnux.dietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The body request to reset password
@Serializable
data class ResetPasswordBodyRequest(
    @SerialName(value = "email_address")
    val emailAddress: String
)