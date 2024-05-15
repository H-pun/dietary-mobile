package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenRequest(
    val idToken: String
)
