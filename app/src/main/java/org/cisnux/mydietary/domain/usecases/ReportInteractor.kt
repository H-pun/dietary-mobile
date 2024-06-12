package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import org.cisnux.mydietary.domain.models.DietProgress
import org.cisnux.mydietary.domain.models.MonthlyNutritionReport
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.WeeklyNutritionReport
import org.cisnux.mydietary.domain.repositories.FoodRepository
import org.cisnux.mydietary.domain.repositories.UserProfileRepository
import org.cisnux.mydietary.commons.utils.Failure
import org.cisnux.mydietary.commons.utils.ReportCategory
import org.cisnux.mydietary.commons.utils.UiState
import org.cisnux.mydietary.commons.utils.fromDateMonthYearToDateAndMonth
import org.cisnux.mydietary.commons.utils.fromMillisToIsoLocalDate
import java.time.Instant
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
class ReportInteractor @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    private val userProfileRepository: UserProfileRepository,
    private val foodRepository: FoodRepository,
) : ReportUseCase {

    override fun getDietProgress(scope: CoroutineScope): Flow<UiState<List<DietProgress>>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope).distinctUntilChanged()
            .filterNotNull()
            .flatMapLatest {
                userProfileRepository.getDietProgress(
                    accessToken = it.second,
                    userId = it.first
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


    override fun getDailyNutrition(scope: CoroutineScope): Flow<UiState<UserNutrition>> =
        authenticationUseCase.getAccessToken(scope = scope)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .combine(userProfileUseCase.getUserProfileDetail()) { accessToken, userProfile ->
                Pair(first = accessToken, userProfile)
            }.flatMapLatest {
                val accessToken = it.first
                userProfileRepository.getUserNutrition(
                    userId = it.second.userAccountId,
                    accessToken = accessToken,
                    date = Instant.now().fromMillisToIsoLocalDate
                )
                    .map { uiState ->
                        if (uiState is UiState.Success) {
                            // user profile
                            val userProfile = it.second
                            val userNutrition = uiState.data!!

                            UiState.Success(
                                data = userProfile.calculateMaxDailyNutrition(userNutrition = userNutrition)
                            )
                        } else uiState
                    }.distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun getDailyNutritionForWidget(scope: CoroutineScope): Flow<UiState<UserNutrition>> =
        authenticationUseCase.getAccessToken(scope = scope)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .combine(userProfileUseCase.getUserProfileDetail()) { accessToken, userProfile ->
                Pair(first = accessToken, userProfile)
            }.flatMapLatest {
                val accessToken = it.first
                userProfileRepository.getUserNutrition(
                    userId = it.second.userAccountId,
                    accessToken = accessToken,
                    date = Instant.now().fromMillisToIsoLocalDate
                )
                    .map { uiState ->
                        if (uiState is UiState.Success) {
                            // user profile
                            val userProfile = it.second
                            val userNutrition = uiState.data!!

                            if (userProfile.userAccountId.isBlank()) UiState.Error(Failure.NotFoundFailure())
                            else UiState.Success(
                                data = userProfile.calculateMaxDailyNutrition(
                                    userNutrition = userNutrition
                                )
                            )
                        } else uiState
                    }.distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
            )


    override fun getWeeklyNutrition(scope: CoroutineScope): Flow<UiState<WeeklyNutritionReport>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope).distinctUntilChanged()
            .filterNotNull()
            .flatMapLatest {
                foodRepository.getFoodDiaryReports(
                    accessToken = it.second,
                    userId = it.first,
                    category = ReportCategory.WEEKLY
                ).map { uiState ->
                    when (uiState) {
                        is UiState.Success -> {
                            val weeklyFoodDiaryReports = uiState.data?.chunked(7) ?: listOf()
                            val weeks = weeklyFoodDiaryReports.map { foodDiaries ->
                                val first = foodDiaries.first().date
                                val last = foodDiaries.last().date
                                if (first != null && last != null) {
                                    WeeklyNutritionReport.DatePage(dateRange = "${first.fromDateMonthYearToDateAndMonth} - ${last.fromDateMonthYearToDateAndMonth}")
                                } else WeeklyNutritionReport.DatePage()
                            }
                            UiState.Success(
                                data = WeeklyNutritionReport(
                                    weeks = weeks,
                                    weekFoodDiaryReports = weeklyFoodDiaryReports
                                )
                            )
                        }

                        is UiState.Error -> {
                            UiState.Error(uiState.error)
                        }

                        is UiState.Loading -> {
                            UiState.Loading
                        }

                        else -> UiState.Initialize
                    }
                }.distinctUntilChanged()
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun getMonthlyNutrition(scope: CoroutineScope): Flow<UiState<MonthlyNutritionReport>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .distinctUntilChanged()
            .filterNotNull()
            .flatMapLatest {
                foodRepository.getFoodDiaryReports(
                    accessToken = it.second,
                    userId = it.first,
                    category = ReportCategory.MONTHLY
                ).map { uiState ->
                    when (uiState) {
                        is UiState.Success -> {
                            UiState.Success(
                                data = MonthlyNutritionReport(
                                    foodDiaryReports = uiState.data ?: listOf()
                                )
                            )
                        }

                        is UiState.Error -> {
                            UiState.Error(uiState.error)
                        }

                        is UiState.Loading -> {
                            UiState.Loading
                        }

                        else -> UiState.Initialize
                    }
                }.distinctUntilChanged()
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

}