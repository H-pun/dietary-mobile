package dev.cisnux.dietary.data.locals

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LandingLocalSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LandingLocalSource {
    override val hasLandingShowed: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[LANDING] ?: false
        }

    override suspend fun updateLandingStatus(hasLandingShowed: Boolean) {
        dataStore.edit { preferences ->
            preferences[LANDING] = hasLandingShowed
        }
    }

    companion object {
        private val LANDING = booleanPreferencesKey("landing")
    }
}