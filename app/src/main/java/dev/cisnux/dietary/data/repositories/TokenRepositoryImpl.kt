package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.data.locals.UserAccountLocalSource
import dev.cisnux.dietary.data.remotes.UserProfileRemoteSource
import dev.cisnux.dietary.domain.repositories.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import dev.cisnux.dietary.utils.Failure

class TokenRepositoryImpl @Inject constructor(
    private val userAccountLocalSource: UserAccountLocalSource,
    private val userProfileRemoteSource: UserProfileRemoteSource
) : TokenRepository {
    override val hasAuthTokenExpired: Flow<Boolean>
        get() = channelFlow {
            userAccountLocalSource.userId.combine(userAccountLocalSource.accessToken) { userId, accessToken ->
                Pair(userId, accessToken)
            }.collectLatest {
                if (it.first != null && it.first?.isNotBlank() == true && it.second != null && it.second?.isNotBlank() == true)
                    userProfileRemoteSource.getUserProfile(
                        userAccountId = it.first!!,
                        accessToken = it.second!!
                    ).fold(
                        ifLeft = { exception ->
                            if (exception is Failure.UnauthorizedFailure)
                                send(true)
                            else send(false)
                        },
                        ifRight = {
                            send(false)
                        }
                    )
                else send(true)
            }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun removeTokenState() {
        userAccountLocalSource.updateAccessToken("")
        userAccountLocalSource.updateUserId("")
    }


}