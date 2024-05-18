package org.cisnux.mydietary.presentation.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.usecases.FoodDiaryUseCase
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import org.cisnux.mydietary.presentation.MainActivity
import org.cisnux.mydietary.presentation.ui.theme.DietaryGlanceTheme
import org.cisnux.mydietary.presentation.ui.theme.DietaryTypeScaleTokens
import org.cisnux.mydietary.presentation.ui.theme.darkRed
import org.cisnux.mydietary.presentation.ui.theme.darkProgress
import org.cisnux.mydietary.presentation.ui.theme.darkYellow
import org.cisnux.mydietary.presentation.ui.theme.lightRed
import org.cisnux.mydietary.presentation.ui.theme.lightProgress
import org.cisnux.mydietary.presentation.ui.theme.lightYellow
import org.cisnux.mydietary.presentation.ui.theme.primaryContainerDark
import org.cisnux.mydietary.presentation.ui.theme.primaryContainerLight
import org.cisnux.mydietary.utils.AppDestination
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.currentLocalDateTimeInBasicISOFormat
import java.time.Instant
import java.util.Locale

class ReportWidget : GlanceAppWidget() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReportWidgetEntryPoint {
        fun userProfileUseCase(): UserProfileUseCase
        fun foodDiaryUseCase(): FoodDiaryUseCase
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext, ReportWidgetEntryPoint::class.java
        )

        provideContent {
            val userProfileUseCase = remember {
                hiltEntryPoint.userProfileUseCase()
            }
            val foodDiaryUseCase = remember {
                hiltEntryPoint.foodDiaryUseCase()
            }
            val oneTimeDailyNutrition by rememberUpdatedState(userProfileUseCase.userDailyNutrition)
            val oneTimeFoodDiary by rememberUpdatedState(
                foodDiaryUseCase.getDiaryFoodsByDaysForWidget(
                    Instant.now().currentLocalDateTimeInBasicISOFormat
                )
            )

            val userDailyNutritionState: MutableState<UiState<UserNutrition>> = remember {
                mutableStateOf(UiState.Initialize)
            }
            val foodDiaryState: MutableState<UiState<List<FoodDiary>>> = remember {
                mutableStateOf(UiState.Initialize)
            }
            LaunchedEffect(Unit) {
                oneTimeDailyNutrition.distinctUntilChanged().collectLatest {
                    userDailyNutritionState.value = it
                }
            }
            LaunchedEffect(Unit) {
                oneTimeFoodDiary.distinctUntilChanged().collectLatest {
                    foodDiaryState.value = it
                }
            }
            val userNutrition =
                if (userDailyNutritionState.value is UiState.Success) (userDailyNutritionState.value as UiState.Success<UserNutrition>).data
                    ?: UserNutrition()
                else UserNutrition()
            val foodDiaries = if (foodDiaryState.value is UiState.Success)
                (foodDiaryState.value as UiState.Success<List<FoodDiary>>).data ?: listOf()
            else listOf()

            DietaryGlanceTheme {
                WidgetContent(
                    totalCarbohydrateToday = userNutrition.totalCarbohydrateToday,
                    totalProteinToday = userNutrition.totalProteinToday,
                    totalCaloriesToday = userNutrition.totalCaloriesToday,
                    totalFatToday = userNutrition.totalFatToday,
                    maxDailyCarbohydrate = userNutrition.maxDailyCarbohydrate,
                    maxDailyProtein = userNutrition.maxDailyProtein,
                    maxDailyCalories = userNutrition.maxDailyCalories,
                    maxDailyFat = userNutrition.maxDailyFat,
                    isSuccess = userDailyNutritionState.value is UiState.Success && foodDiaryState.value is UiState.Success,
                    foodDiaries = foodDiaries,
                    context = context
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        context: Context,
        modifier: GlanceModifier = GlanceModifier,
        foodDiaries: List<FoodDiary> = listOf(),
        totalCaloriesToday: Float = 0f,
        totalCarbohydrateToday: Float = 0f,
        totalProteinToday: Float = 0f,
        totalFatToday: Float = 0f,
        maxDailyCalories: Float = 0f,
        maxDailyCarbohydrate: Float = 0f,
        maxDailyProtein: Float = 0f,
        maxDailyFat: Float = 0f,
        isSuccess: Boolean = false,
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize().appWidgetBackground()
                .background(GlanceTheme.colors.surface).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isSuccess) {
                item {
                    Column(modifier = GlanceModifier.fillMaxWidth()) {
                        Text(
                            text = "✦ Nutrisimu saat ini",
                            style = TextStyle(
                                fontFamily = FontFamily("roboto"),
                                fontWeight = FontWeight.Medium,
                                fontSize = DietaryTypeScaleTokens.TitleMediumSize,
                                color = GlanceTheme.colors.onSurface,
                                textAlign = TextAlign.Start
                            ),
                        )
                        Spacer(GlanceModifier.height(4.dp))
                    }
                }
                item {
                    val deepLinkIntent = Intent(
                        Intent.ACTION_VIEW,
                        AppDestination.ReportRoute.createDeepLinkUrl(),
                        context,
                        MainActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val action = actionStartActivity(intent = deepLinkIntent)
                    Column(
                        modifier = GlanceModifier.fillMaxWidth().cornerRadius(8.dp)
                            .clickable(onClick = action)
                    ) {
                        repeat(4) {
                            val label = when (it) {
                                0 -> "Kalori"
                                1 -> "Karbohidrat"
                                2 -> "Protein"
                                else -> "Lemak"
                            }
                            val unit = if (it != 0) "g" else "kcal"
                            val totalNutrition = when (it) {
                                0 -> totalCaloriesToday
                                1 -> totalCarbohydrateToday
                                2 -> totalProteinToday
                                else -> totalFatToday
                            }
                            val maxNutrition = when (it) {
                                0 -> maxDailyCalories
                                1 -> maxDailyCarbohydrate
                                2 -> maxDailyProtein
                                else -> maxDailyFat
                            }
                            val percentage = when (it) {
                                0 -> totalCaloriesToday / maxDailyCalories
                                1 -> totalCarbohydrateToday / maxDailyCarbohydrate
                                2 -> totalProteinToday / maxDailyProtein
                                else -> totalFatToday / maxDailyFat
                            }
                            val darkColor = when {
                                percentage > 1.0f -> darkRed
                                percentage > 0.5f -> darkYellow
                                else -> primaryContainerDark
                            }
                            val lightColor = when {
                                percentage > 1.0f -> lightRed
                                percentage > 0.5f -> lightYellow
                                else -> primaryContainerLight
                            }
                            val locale = rememberSaveable {
                                Locale("id", "ID")
                            }

                            Column {
                                Row(
                                    modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${
                                            String.format(
                                                locale,
                                                "%.2f",
                                                totalNutrition
                                            )
                                        } $unit",
                                        style = TextStyle(
                                            fontFamily = FontFamily("roboto"),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = DietaryTypeScaleTokens.LabelMediumSize,
                                            color = GlanceTheme.colors.onSurface,
                                            textAlign = TextAlign.Start
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = "${
                                            String.format(
                                                locale,
                                                "%.2f",
                                                maxNutrition
                                            )
                                        } $unit",
                                        style = TextStyle(
                                            fontFamily = FontFamily("roboto"),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = DietaryTypeScaleTokens.LabelMediumSize,
                                            color = GlanceTheme.colors.onSurface,
                                            textAlign = TextAlign.End
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                }
                                Spacer(GlanceModifier.height(2.dp))
                                LinearProgressIndicator(
                                    progress = percentage, color = ColorProvider(
                                        day = lightColor, night = darkColor
                                    ), backgroundColor = ColorProvider(
                                        day = lightProgress, night = darkProgress
                                    ), modifier = GlanceModifier.fillMaxWidth().height(12.dp)
                                )
                                Spacer(GlanceModifier.height(2.dp))
                                Row(
                                    modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = label, style = TextStyle(
                                            fontFamily = FontFamily("roboto"),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = DietaryTypeScaleTokens.LabelMediumSize,
                                            color = GlanceTheme.colors.onSurface,
                                            textAlign = TextAlign.Start
                                        ), modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = "Maks $label", style = TextStyle(
                                            fontFamily = FontFamily("roboto"),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = DietaryTypeScaleTokens.LabelMediumSize,
                                            color = GlanceTheme.colors.onSurface,
                                            textAlign = TextAlign.End
                                        ), modifier = GlanceModifier.defaultWeight()
                                    )
                                }
                                Spacer(GlanceModifier.height(4.dp))
                            }
                        }
                    }
                }
                item {
                    if (foodDiaries.isNotEmpty())
                        Column(modifier = GlanceModifier.fillMaxWidth()) {
                            Text(
                                text = "✦ Diary makananmu",
                                style = TextStyle(
                                    fontFamily = FontFamily("roboto"),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = DietaryTypeScaleTokens.TitleMediumSize,
                                    color = GlanceTheme.colors.onSurface,
                                    textAlign = TextAlign.Start
                                ),
                            )
                            Spacer(GlanceModifier.height(4.dp))
                        }
                }
                items(
                    foodDiaries.size,
                    itemId = {
                        foodDiaries[it].hashCode().toLong()
                    }) {
                    val locale = Locale("id", "ID")
                    val deepLinkIntent = Intent(
                        Intent.ACTION_VIEW,
                        AppDestination.DiaryDetailRoute.createDeepLinkUrl(
                            foodDiaries[it].id,
                            isWidget = true
                        ),
                        context,
                        MainActivity::class.java
                    )
                    val action = actionStartActivity(intent = deepLinkIntent)

                    Column {
                        Column(
                            modifier = GlanceModifier.fillMaxWidth().padding(8.dp)
                                .cornerRadius(8.dp).clickable(onClick = action)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalAlignment = Alignment.Start,
                                modifier = GlanceModifier.fillMaxWidth()
                            ) {
                                val foodPictureFile = foodDiaries[it].foodPictureFile
                                foodPictureFile?.let {bitmap ->
                                    Image(
                                        provider = ImageProvider(bitmap),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = GlanceModifier.size(24.dp).cornerRadius(8.dp)
                                    )
                                } ?: Spacer(
                                    GlanceModifier.size(24.dp).cornerRadius(8.dp).background(
                                        ColorProvider(
                                            day = lightProgress,
                                            night = darkProgress
                                        )
                                    )
                                )
                                Spacer(GlanceModifier.width(4.dp))
                                Column(modifier = GlanceModifier.fillMaxWidth()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = GlanceModifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = foodDiaries[it].title,
                                            modifier = GlanceModifier.defaultWeight(),
                                            style = TextStyle(
                                                fontFamily = FontFamily("roboto"),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = DietaryTypeScaleTokens.TitleSmallSize,
                                                color = GlanceTheme.colors.onSurface,
                                                textAlign = TextAlign.Start
                                            )
                                        )
                                        Text(
                                            text = "${
                                                String.format(
                                                    locale,
                                                    "%.2f",
                                                    foodDiaries[it].totalFoodCalories
                                                )
                                            } kcal",
                                            modifier = GlanceModifier.defaultWeight(),
                                            style = TextStyle(
                                                fontFamily = FontFamily("roboto"),
                                                fontSize = DietaryTypeScaleTokens.LabelMediumSize,
                                                color = GlanceTheme.colors.onSurface,
                                                textAlign = TextAlign.End
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = foodDiaries[it].date,
                                style = TextStyle(
                                    fontFamily = FontFamily("roboto"),
                                    fontSize = DietaryTypeScaleTokens.BodySmallSize,
                                    color = GlanceTheme.colors.onSurface
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = foodDiaries[it].time,
                                style = TextStyle(
                                    fontFamily = FontFamily("roboto"),
                                    fontSize = DietaryTypeScaleTokens.LabelSmallSize,
                                    color = GlanceTheme.colors.onSurface
                                )
                            )
                        }
                        Spacer(GlanceModifier.height(8.dp))
                    }
                }
            } else item {
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.empty_report),
                        contentDescription = null,
                        modifier = GlanceModifier.size(190.dp)
                    )
                    Spacer(GlanceModifier.height(4.dp))
                    Text(
                        text = "Belum ada data yang dapat ditampilkan",
                        style = TextStyle(
                            fontFamily = FontFamily("roboto"),
                            fontWeight = FontWeight.Medium,
                            fontSize = DietaryTypeScaleTokens.LabelLargeSize,
                            color = GlanceTheme.colors.onSurface
                        ),
                    )
                }
            }
        }
    }
}