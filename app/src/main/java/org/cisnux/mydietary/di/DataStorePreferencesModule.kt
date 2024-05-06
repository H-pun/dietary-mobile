package org.cisnux.mydietary.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.UserProfile
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "authentication")
val Context.userProfile: DataStore<UserProfile> by dataStore(
    fileName = "user_profile.pb",
    serializer = object : Serializer<UserProfile> {
        override val defaultValue: UserProfile
            get() = UserProfile.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): UserProfile = try {
            UserProfile.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

        override suspend fun writeTo(t: UserProfile, output: OutputStream) =
            t.writeTo(output)
    }
)

@Module
@InstallIn(SingletonComponent::class)
object DataStorePreferencesModule {
    @Singleton
    @Provides
    fun providePreferenceDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> = applicationContext.dataStore

    @Singleton
    @Provides
    fun provideUserProfileDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<UserProfile> = applicationContext.userProfile
}