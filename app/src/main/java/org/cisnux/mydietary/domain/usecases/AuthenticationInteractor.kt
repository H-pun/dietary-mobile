package org.cisnux.mydietary.domain.usecases

import android.app.Activity
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineScope
import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.domain.repositories.AuthenticationRepository
import org.cisnux.mydietary.domain.repositories.UserProfileRepository
import org.cisnux.mydietary.utils.AuthenticationState
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import org.cisnux.mydietary.utils.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.cisnux.mydietary.domain.models.ChangePassword
import org.cisnux.mydietary.domain.models.ForgotPassword

@ExperimentalCoroutinesApi
class AuthenticationInteractor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val firebaseAuth: FirebaseAuth,
    private val credentialRequest: GetCredentialRequest,
    private val credentialManager: CredentialManager,
) : AuthenticationUseCase {

    override fun getAuthenticationState(scope: CoroutineScope): Flow<AuthenticationState> =
        getAccessTokenAndUserId(scope = scope).flatMapLatest {
            it?.let {
                userProfileRepository.getUserProfile(accessToken = it.second, userId = it.first)
                    .map { uiSate ->
                        when (uiSate) {
                            is UiState.Success -> AuthenticationState.HAS_SIGNED_IN_AND_USER_PROFILE
                            is UiState.Error -> when (uiSate.error) {
                                is Failure.UnauthorizedFailure -> AuthenticationState.HAS_NOT_SIGNED_IN
                                is Failure.NotFoundFailure -> AuthenticationState.HAS_NOT_USER_PROFILE
                                else -> AuthenticationState.UNKNOWN
                            }

                            else -> AuthenticationState.INITIALIZE
                        }
                    }
                    .distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = AuthenticationState.INITIALIZE
                    )
            } ?: flow { emit(AuthenticationState.HAS_NOT_SIGNED_IN) }
                .flowOn(Dispatchers.IO)
                .stateIn(
                    scope = scope,
                    started = SharingStarted.Lazily,
                    initialValue = AuthenticationState.INITIALIZE
                )
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
            )

    override fun getAccessTokenAndUserId(scope: CoroutineScope): Flow<Pair<String, String>?> =
        getUserId(scope = scope)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .combine(
                getAccessToken(scope = scope).filterNotNull().filter { it.isNotBlank() }
                    .distinctUntilChanged()
            ) { userId, accessToken ->
                Pair(first = userId, second = accessToken)
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
            )

    override fun getAccessToken(scope: CoroutineScope): Flow<String?> =
        authenticationRepository.accessToken
            .flowOn(Dispatchers.IO)
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
            )

    override fun getUserId(scope: CoroutineScope): Flow<String?> =
        authenticationRepository.userId
            .flowOn(Dispatchers.IO)
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
            )

    override fun signInWithEmailAndPassword(
        userAccount: UserAccount,
        scope: CoroutineScope
    ): Flow<UiState<Nothing>> =
        authenticationRepository.verifyUserAccount(userAccount)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun signInWithGoogle(
        scope: CoroutineScope,
        context: Activity
    ): Flow<UiState<Nothing>> {
        return channelFlow {
            trySend(UiState.Loading)
            try {
                handleSignInWithGoogle(context = context)?.let { googleIdToken ->
                    val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = firebaseAuth.signInWithCredential(credential).await()
                    val authUser = authResult.user
                    val googleToken = authUser?.getIdToken(true)?.await()?.token
                    googleToken?.let { token ->
                        authenticationRepository.verifyGoogleAccount(token = token)
                            .distinctUntilChanged()
                            .flowOn(Dispatchers.IO)
                            .stateIn(
                                scope = scope,
                                started = SharingStarted.Lazily,
                                initialValue = UiState.Initialize
                            )
                            .collectLatest { uiState ->
                                trySend(uiState)
                            }
                    } ?: trySend(UiState.Error(Failure.UnauthorizedFailure("token is invalid")))
                } ?: trySend(UiState.Initialize)
                // change to flow
            } catch (e: FirebaseAuthInvalidUserException) {
                trySend(UiState.Error(e))
            } catch (e: FirebaseAuthUserCollisionException) {
                trySend(UiState.Error(e))
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                trySend(UiState.Error(e))
            } catch (e: IOException) {
                trySend(UiState.Error(Failure.ConnectionFailure("no internet connection")))
            } catch (e: Exception) {
                trySend(UiState.Error(e))
            }
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )
    }

    private suspend fun handleSignInWithGoogle(context: Activity): String? =
        withContext(Dispatchers.IO) {
            try {
                val result = credentialManager.getCredential(
                    request = credentialRequest,
                    context = context,
                )
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            try {
                                // Use googleIdTokenCredential and extract id to validate and
                                // authenticate on your server.
                                val googleIdTokenCredential = GoogleIdTokenCredential
                                    .createFrom(credential.data)
                                googleIdTokenCredential.idToken
                            } catch (e: GoogleIdTokenParsingException) {
                                Log.e(
                                    AuthenticationUseCase::class.simpleName,
                                    "Received an invalid google id token response",
                                    e
                                )
                                null
                            }
                        } else {
                            // Catch any unrecognized credential type here.
                            Log.e(
                                AuthenticationUseCase::class.simpleName,
                                "Unexpected type of credential"
                            )
                            null
                        }
                    }

                    else -> {
                        // Catch any unrecognized credential type here.
                        Log.e(
                            AuthenticationUseCase::class.simpleName,
                            "Unexpected type of credential"
                        )
                        null
                    }
                }
            } catch (e: GetCredentialException) {
                Log.e(
                    AuthenticationUseCase::class.simpleName,
                    e.message.toString()
                )
                null
            }
        }

    override fun signUpWithEmailAndPassword(
        userAccount: UserAccount,
        scope: CoroutineScope
    ): Flow<UiState<Nothing>> =
        authenticationRepository.addUserAccount(userAccount)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun sendResetPassword(
        emailAddress: String,
        scope: CoroutineScope
    ): Flow<UiState<String>> =
        authenticationRepository.resetPassword(emailAddress)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun forgotPassword(
        forgotPassword: ForgotPassword,
        scope: CoroutineScope
    ): Flow<UiState<String>> =
        authenticationRepository.updatePassword(forgotPassword = forgotPassword)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun changePassword(
        changePassword: ChangePassword,
        scope: CoroutineScope
    ): Flow<UiState<String>> =
        getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                authenticationRepository.changePassword(
                    accessToken = it.second,
                    id = it.first,
                    changePassword = changePassword
                ).distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun changeEmail(newEmail: String, scope: CoroutineScope): Flow<UiState<String>> =
        getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                authenticationRepository.changeEmail(
                    accessToken = it.second,
                    id = it.first,
                    email = newEmail
                ).distinctUntilChanged()
                    .flatMapLatest { uiState ->
                        when (uiState) {
                            is UiState.Success -> userProfileRepository.getUserProfile(
                                accessToken = it.second,
                                userId = it.first
                            )
                            is UiState.Error -> flow { emit(uiState) }
                            is UiState.Loading -> flow { emit(uiState) }
                            is UiState.Initialize -> flow { emit(uiState) }
                        }
                    }
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun verifyEmail(email: String, scope: CoroutineScope): Flow<UiState<String>> =
        getAccessToken(scope = scope)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .flatMapLatest { accessToken ->
                authenticationRepository
                    .verifyEmail(accessToken = accessToken, email = email)
                    .distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            credentialManager.clearCredentialState(request = ClearCredentialStateRequest())
        } finally {
            authenticationRepository.deleteSession()
            userProfileRepository.deleteCurrentUserProfile()
        }
    }
}