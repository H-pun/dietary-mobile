package org.cisnux.mydietary.di

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.commons.utils.WEB_CLIENT_ID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GetSignInWithGoogleOptionModule {

    @Singleton
    @Provides
    fun provideGetSignInWithGoogleOption(): GetSignInWithGoogleOption {
        return GetSignInWithGoogleOption.Builder(serverClientId = WEB_CLIENT_ID)
            .setNonce(nonce = null)
            .build()
    }
}