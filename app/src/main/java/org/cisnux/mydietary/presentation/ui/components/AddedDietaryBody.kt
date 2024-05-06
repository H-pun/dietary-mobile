package org.cisnux.mydietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.Food
import org.cisnux.mydietary.presentation.ui.theme.darkRed
import org.cisnux.mydietary.presentation.ui.theme.darkYellow
import org.cisnux.mydietary.presentation.ui.theme.lightRed
import org.cisnux.mydietary.presentation.ui.theme.lightYellow
import org.cisnux.mydietary.presentation.ui.theme.onSurfaceDark
import org.cisnux.mydietary.presentation.ui.theme.onSurfaceLight
import org.cisnux.mydietary.presentation.ui.theme.secondaryContainerDark
import org.cisnux.mydietary.presentation.ui.theme.secondaryContainerLight
import org.cisnux.mydietary.presentation.ui.theme.surfaceDark
import org.cisnux.mydietary.presentation.ui.theme.surfaceLight
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddedDietaryBody(
    totalCaloriesToday: Float,
    totalCarbohydrateToday: Float,
    totalProteinToday: Float,
    totalFatToday: Float,
    maxDailyCalories: Float,
    maxDailyCarbohydrate: Float,
    maxDailyProtein: Float,
    maxDailyFat: Float,
    totalFoodCalories: Float,
    totalFoodCarbohydrate: Float,
    totalFoodProtein: Float,
    totalFoodFat: Float,
    foods: List<Food>,
    modifier: Modifier = Modifier,
    bottomContent: @Composable () -> Unit = {},
    feedback: List<String> = listOf()
) {
    val context = LocalContext.current
    val caloriePercentage = totalCaloriesToday / maxDailyCalories
    val carbohydratePercentage = totalCarbohydrateToday / maxDailyCarbohydrate
    val proteinPercentage = totalProteinToday / maxDailyProtein
    val fatPercentage = totalFatToday / maxDailyFat
    val nestedScrolling = rememberNestedScrollInteropConnection()
    val locale = rememberSaveable {
        Locale("id", "ID")
    }
    val calorieDarkColor = when {
        caloriePercentage > 1.0f -> darkRed
        caloriePercentage > 0.5f -> darkYellow
        else -> secondaryContainerDark
    }
    val carbohydrateDarkColor = when {
        carbohydratePercentage > 1.0f -> darkRed
        carbohydratePercentage > 0.5f -> darkYellow
        else -> secondaryContainerDark
    }
    val proteinDarkColor = when {
        proteinPercentage > 1.0f -> darkRed
        proteinPercentage > 0.5f -> darkYellow
        else -> secondaryContainerDark
    }
    val fatDarkColor = when {
        fatPercentage > 1.0f -> darkRed
        fatPercentage > 0.5f -> darkYellow
        else -> secondaryContainerDark
    }
    val calorieLightColor = when {
        caloriePercentage > 1.0f -> lightRed
        caloriePercentage > 0.5f -> lightYellow
        else -> secondaryContainerLight
    }
    val carbohydrateLightColor = when {
        carbohydratePercentage > 1.0f -> lightRed
        carbohydratePercentage > 0.5f -> lightYellow
        else -> secondaryContainerLight
    }
    val proteinLightColor = when {
        proteinPercentage > 1.0f -> lightRed
        proteinPercentage > 0.5f -> lightYellow
        else -> secondaryContainerLight
    }
    val fatLightColor = when {
        fatPercentage > 1.0f -> lightRed
        fatPercentage > 0.5f -> lightYellow
        else -> secondaryContainerLight
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .nestedScroll(nestedScrolling)
            .fillMaxWidth()
    ) {
        item {
            Text(
                text = "✧ Nutrisimu saat ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> onSurfaceLight
                                    else -> onSurfaceDark
                                }
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawArc(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> calorieLightColor
                                    else -> calorieDarkColor
                                },
                                startAngle = 270f,
                                sweepAngle = 360f * caloriePercentage,
                                useCenter = true
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(105.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> surfaceLight
                                    else -> surfaceDark
                                }
                            )
                        })
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Text(
                            text = String.format(locale, "%.2f", totalCaloriesToday),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = String.format(locale, "%.2f kcal", maxDailyCalories),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kalori/\nMax Kalori",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(contentAlignment = Alignment.Center) {
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> onSurfaceLight
                                    else -> onSurfaceDark
                                }
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawArc(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> carbohydrateLightColor
                                    else -> carbohydrateDarkColor
                                },
                                startAngle = 270f,
                                sweepAngle = 360f * carbohydratePercentage,
                                useCenter = true
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(105.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> surfaceLight
                                    else -> surfaceDark
                                }
                            )
                        })
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .height(90.dp)
                            .width(100.dp)
                    ) {
                        Text(
                            text = String.format(locale, "%.2f", totalCarbohydrateToday),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = String.format(locale, "%.2f g", maxDailyCarbohydrate),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Karbohidrat/\nMax Karbo",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(contentAlignment = Alignment.Center) {
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> onSurfaceLight
                                    else -> onSurfaceDark
                                }
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawArc(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> proteinLightColor
                                    else -> proteinDarkColor
                                },
                                startAngle = 270f,
                                sweepAngle = 360f * proteinPercentage,
                                useCenter = true
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(105.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> surfaceLight
                                    else -> surfaceDark
                                }
                            )
                        })
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Text(
                            text = String.format(locale, "%.2f", totalProteinToday),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = String.format(locale, "%.2f g", maxDailyProtein),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Protein/\nMax Protein",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(contentAlignment = Alignment.Center) {
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> onSurfaceLight
                                    else -> onSurfaceDark
                                }
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(115.dp)
                        .drawBehind {
                            drawArc(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> fatLightColor
                                    else -> fatDarkColor
                                },
                                startAngle = 270f,
                                sweepAngle = 360f * fatPercentage,
                                useCenter = true
                            )
                        })
                    Spacer(modifier = Modifier
                        .size(105.dp)
                        .drawBehind {
                            drawCircle(
                                color = when (context.resources.configuration.uiMode) {
                                    Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> surfaceLight
                                    else -> surfaceDark
                                }
                            )
                        })
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Text(
                            text = String.format(locale, "%.2f", totalFatToday),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = String.format(locale, "%.2f g", maxDailyFat),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lemak/\nMax Lemak",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(foods,
            key = { it.id },
            contentType = { it }) {
            FoodListItem(
                foodName = it.name,
                calorie = String.format(locale, "%.2f", it.calories),
                fat = String.format(locale, "%.2f", it.fat),
                carbohydrates = String.format(locale, "%.2f", it.carbohydrates),
                protein = String.format(locale, "%.2f", it.protein),
                sugar = it.sugar?.let { sugar -> String.format(locale, "%.2f", sugar) },
                feedback = it.feedback,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            var isFeedbackExpanded by rememberSaveable {
                mutableStateOf(false)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.total_food_calories),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(locale, "%.2f kcal", totalFoodCalories),
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total Karbohidrat Makanan",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(locale, "%.2f g", totalFoodCarbohydrate),
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total Protein Makanan",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(locale, "%.2f g", totalFoodProtein),
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total Lemak Makanan",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(locale, "%.2f g", totalFoodFat),
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.5.dp)
            if (feedback.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFeedbackExpanded = !isFeedbackExpanded }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(id = R.string.feedback),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFeedbackExpanded)
                }
                HorizontalDivider(thickness = 1.5.dp)
            }
            AnimatedVisibility(visible = isFeedbackExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        feedback.forEach { note ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = note,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(thickness = 1.5.dp)
                        }
                    }
                }
            }
            bottomContent()
        }
    }
}

