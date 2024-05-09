package org.cisnux.mydietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.presentation.ui.theme.commonCircularCardColor
import org.cisnux.mydietary.presentation.ui.theme.darkRed
import org.cisnux.mydietary.presentation.ui.theme.darkYellow
import org.cisnux.mydietary.presentation.ui.theme.lightRed
import org.cisnux.mydietary.presentation.ui.theme.lightYellow
import org.cisnux.mydietary.presentation.ui.theme.secondaryContainerDark
import org.cisnux.mydietary.presentation.ui.theme.secondaryContainerLight
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
){
    val context = LocalContext.current
    val caloriePercentage = totalCaloriesToday / maxDailyCalories
    val carbohydratePercentage = totalCarbohydrateToday / maxDailyCarbohydrate
    val proteinPercentage = totalProteinToday / maxDailyProtein
    val fatPercentage = totalFatToday / maxDailyFat
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
    val onSurfaceColor = commonCircularCardColor

    Row(
        modifier = modifier
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
                        color = onSurfaceColor
                    )
                })
            Spacer(modifier = Modifier
                .size(115.dp)
                .drawBehind {
                    drawArc(
                        color = when (context.resources.configuration.uiMode) {
                            Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> calorieDarkColor
                            else -> calorieLightColor
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
                        color = onSurfaceColor
                    )
                })
            Spacer(modifier = Modifier
                .size(115.dp)
                .drawBehind {
                    drawArc(
                        color = when (context.resources.configuration.uiMode) {
                            Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> carbohydrateDarkColor
                            else -> carbohydrateLightColor
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
                        color = onSurfaceColor
                    )
                })
            Spacer(modifier = Modifier
                .size(115.dp)
                .drawBehind {
                    drawArc(
                        color = when (context.resources.configuration.uiMode) {
                            Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> proteinDarkColor
                            else -> proteinLightColor
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
                        color = onSurfaceColor
                    )
                })
            Spacer(modifier = Modifier
                .size(115.dp)
                .drawBehind {
                    drawArc(
                        color = when (context.resources.configuration.uiMode) {
                            Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> fatDarkColor
                            else -> fatLightColor
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
}