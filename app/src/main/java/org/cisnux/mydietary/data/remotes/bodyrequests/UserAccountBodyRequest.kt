package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The body request to sign in and sign up.
@Serializable
data class UserAccountBodyRequest(
    @SerialName(value = "email")
    val emailAddress: String,
    val password: String,
)