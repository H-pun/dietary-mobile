package dev.cisnux.dietary.data.locals

import kotlinx.coroutines.flow.Flow

interface UserAccountLocalSource {
    val userId: Flow<String?>
    val accessToken: Flow<String?>
    suspend fun updateUserId(userId: String)
    suspend fun updateAccessToken(accessToken: String)
}