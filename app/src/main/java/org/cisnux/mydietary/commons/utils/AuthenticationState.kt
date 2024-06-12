package org.cisnux.mydietary.commons.utils

enum class AuthenticationState {
    INITIALIZE,
    HAS_NOT_SIGNED_IN,
    HAS_NOT_USER_PROFILE,
    UNKNOWN,
    HAS_SIGNED_IN_AND_USER_PROFILE
}