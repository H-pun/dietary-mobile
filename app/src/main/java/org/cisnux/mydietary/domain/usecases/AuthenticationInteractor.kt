package org.cisnux.mydietary.domain.usecases

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import io.ktor.utils.io.errors.IOException
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
class AuthenticationInteractor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val firebaseAuth: FirebaseAuth
) : AuthenticationUseCase {
    override val accessToken: Flow<String?>
        get() = authenticationRepository.accessToken
    override val userId: Flow<String?>
        get() = authenticationRepository.userId
    override val isAccessTokenAndUserIdExists: Flow<Pair<String, String>?>
        get() = userId.combine(accessToken) { userId, accessToken ->
            Pair(first = userId, second = accessToken)
        }.map {
            if (it.first != null && it.first?.isNotBlank() == true && it.second != null && it.second?.isNotBlank() == true)
                Pair(
                    first = it.first!!,
                    second = it.second!!
                )
            else null
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    override val authenticationState: Flow<AuthenticationState>
        get() = isAccessTokenAndUserIdExists.flatMapLatest {
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
            } ?: flow { emit(AuthenticationState.HAS_NOT_SIGNED_IN) }
        }

    override fun signInWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
        authenticationRepository.signInWithEmailAndPassword(userAccount)

    override fun signInWithGoogle(token: String): Flow<UiState<Nothing>> = flow {
        emit(UiState.Loading)
        try {
            val credential = GoogleAuthProvider.getCredential(token, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val authUser = authResult.user
            val idToken = authUser?.getIdToken(true)?.await()?.token
            Log.d(AuthenticationUseCase::class.simpleName, idToken?: "null")
            emit(UiState.Success(null))
        } catch (e: FirebaseAuthInvalidUserException) {
            emit(UiState.Error(e))
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(UiState.Error(e))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(UiState.Error(e))
        } catch (e: IOException) {
            emit(UiState.Error(Failure.ConnectionFailure("no internet connection")))
        } catch (e: Exception) {
            emit(UiState.Error(e))
        }
    }

    override fun signUpWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
        authenticationRepository.signUpWithEmailAndPassword(userAccount)

    override fun signUpWithGoogle(): Flow<UiState<Nothing>> =
        authenticationRepository.signUpWithGoogle()

    override fun resetPassword(emailAddress: String): Flow<UiState<Nothing>> =
        authenticationRepository.resetPassword(emailAddress)

    override suspend fun signOut() = authenticationRepository.signOut()
}