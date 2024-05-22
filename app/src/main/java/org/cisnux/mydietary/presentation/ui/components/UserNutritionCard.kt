package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.darkRed
import org.cisnux.mydietary.presentation.ui.theme.darkProgress
import org.cisnux.mydietary.presentation.ui.theme.darkYellow
import org.cisnux.mydietary.presentation.ui.theme.lightRed
import org.cisnux.mydietary.presentation.ui.theme.lightProgress
import org.cisnux.mydietary.presentation.ui.theme.lightYellow
import org.cisnux.mydietary.presentation.ui.theme.primaryContainerDark
import org.cisnux.mydietary.presentation.ui.theme.primaryContainerLight
import org.cisnux.mydietary.presentation.ui.theme.surfaceDark
import org.cisnux.mydietary.presentation.ui.theme.surfaceLight
import java.util.Locale

@Composable
fun UserNutritionCard(
    totalCaloriesToday: Float,
    totalCarbohydrateToday: Float,
    totalProteinToday: Float,
    totalFatToday: Float,
    maxDailyCalories: Float,
    maxDailyCarbohydrate: Float,
    maxDailyProtein: Float,
    maxDailyFat: Float,
    modifier: Modifier = Modifier
) {
    val locale = rememberSaveable {
        Locale.getDefault()
    }
    val onSurfaceColor = if (!isSystemInDarkTheme()) lightProgress
    else darkProgress

    val surfaceColor = if (!isSystemInDarkTheme()) surfaceLight
    else surfaceDark

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(4) {
            val label = stringResource(id =when (it) {
                0 -> R.string.calories
                1 -> R.string.carbohydrates
                2 -> R.string.protein
                else -> R.string.fat
            })
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
            val wheelColor = when {
                percentage > 1.0f -> if (!isSystemInDarkTheme()) lightRed else darkRed
                percentage > 0.5f -> if (!isSystemInDarkTheme()) lightYellow else darkYellow
                else -> if (!isSystemInDarkTheme()) primaryContainerLight else primaryContainerDark
            }

            Box(contentAlignment = Alignment.Center) {
                Spacer(modifier = Modifier
                    .size(135.dp)
                    .drawBehind {
                        drawCircle(
                            color = onSurfaceColor
                        )
                    })
                Spacer(modifier = Modifier
                    .size(135.dp)
                    .drawBehind {
                        drawArc(
                            color = wheelColor,
                            startAngle = 270f,
                            sweepAngle = 360f * percentage,
                            useCenter = true
                        )
                    })
                Spacer(modifier = Modifier
                    .size(125.dp)
                    .drawBehind {
                        drawCircle(
                            color = surfaceColor
                        )
                    })
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.size(90.dp)
                ) {
                    Text(
                        text = String.format(locale, "%.2f", totalNutrition),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = String.format(locale, "%.2f $unit", maxNutrition),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.wheel_max_label, label, label),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}