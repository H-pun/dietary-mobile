package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The response of sign in
@Serializable
data class SignInResponse(
    @SerialName(value = "access_token")
    val accessToken: String
)
