package org.cisnux.mydietary.data.locals

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import org.cisnux.mydietary.utils.DIETARY_API
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BaseApiUrlLocalSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : BaseApiUrlLocalSource {
    override val baseApiUrl: Flow<String>
        get() = dataStore.data.map { preferences ->
            preferences[BASE_URL] ?: DIETARY_API
        }

    override suspend fun updateBaseApiUrl(baseApiUrl: String) {
        dataStore.edit { preferences ->
            preferences[BASE_URL] = baseApiUrl
        }
    }

    companion object {
        private val BASE_URL = stringPreferencesKey("base_url")
    }
}