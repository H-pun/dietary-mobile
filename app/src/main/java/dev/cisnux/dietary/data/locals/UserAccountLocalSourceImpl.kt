package dev.cisnux.dietary.data.locals

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserAccountLocalSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserAccountLocalSource {
    override val userId: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    override val accessToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    override suspend fun updateUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    override suspend fun updateAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }


    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }
}