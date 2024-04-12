package dev.cisnux.dietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.presentation.ui.theme.mediumErrorContainerDark
import dev.cisnux.dietary.presentation.ui.theme.mediumErrorContainerLight
import dev.cisnux.dietary.presentation.ui.theme.onMediumErrorContainerDark
import dev.cisnux.dietary.presentation.ui.theme.onMediumErrorContainerLight
import dev.cisnux.dietary.presentation.ui.theme.onSurfaceDark
import dev.cisnux.dietary.presentation.ui.theme.onSurfaceLight
import dev.cisnux.dietary.presentation.ui.theme.secondaryContainerDark
import dev.cisnux.dietary.presentation.ui.theme.secondaryContainerLight
import dev.cisnux.dietary.presentation.ui.theme.surfaceDark
import dev.cisnux.dietary.presentation.ui.theme.surfaceLight

@Composable
fun AddedDietaryBody(
    totalUserCaloriesToday: Float,
    userDailyBmiCalorie: Float,
    totalFoodCalories: Float,
    foods: List<Food>,
    feedback: String?,
    status: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val percentage = totalUserCaloriesToday / userDailyBmiCalorie

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp), modifier = modifier
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kalorimu Hari Ini\n/Maks Kalori",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(contentAlignment = Alignment.Center) {
                        Spacer(modifier = Modifier
                            .size(100.dp)
                            .drawBehind {
                                drawCircle(
                                    color = when (context.resources.configuration.uiMode) {
                                        Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> onSurfaceLight
                                        else -> onSurfaceDark
                                    }
                                )
                            })
                        Spacer(modifier = Modifier
                            .size(100.dp)
                            .drawBehind {
                                drawArc(
                                    color = when (context.resources.configuration.uiMode) {
                                        Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> secondaryContainerLight
                                        else -> secondaryContainerDark
                                    },
                                    startAngle = 270f,
                                    sweepAngle = 360f * percentage,
                                    useCenter = true
                                )
                            })
                        Spacer(modifier = Modifier
                            .size(90.dp)
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
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text(
                                text = String.format("%.2f", totalUserCaloriesToday),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = String.format("/%.2f kcal", userDailyBmiCalorie),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        val foodStatusLevels = stringArrayResource(id = R.array.food_status_level)
                        Text(
                            text = stringResource(R.string.status),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = when (status.lowercase()) {
                                foodStatusLevels[0] -> MaterialTheme.colorScheme.primaryContainer
                                foodStatusLevels[1] -> if (isSystemInDarkTheme()) mediumErrorContainerDark
                                else mediumErrorContainerLight

                                else -> MaterialTheme.colorScheme.errorContainer
                            },
                        ) {
                            Text(
                                text = status,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge,
                                color = when (status.lowercase()) {
                                    foodStatusLevels[0] -> MaterialTheme.colorScheme.onPrimaryContainer
                                    foodStatusLevels[1] -> if (isSystemInDarkTheme()) onMediumErrorContainerDark
                                    else onMediumErrorContainerLight

                                    else -> MaterialTheme.colorScheme.onErrorContainer
                                },
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(thickness = 1.5.dp)
                    feedback?.let { feedback ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = stringResource(R.string.feedback),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = feedback,
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(130.dp),
                                textAlign = TextAlign.End
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.5.dp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
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
                            text = String.format("%.2f kcal", totalFoodCalories),
                            fontWeight = FontWeight.Light,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(thickness = 1.5.dp)
                }
            }
        }
        items(foods,
//            key = { it.id },
            contentType = { it }) {
            FoodListItem(
                foodName = it.name,
                calorie = String.format("%.2f", it.calorie),
                fat = String.format("%.2f", it.fat),
                carbohydrates = String.format("%.2f", it.carbohydrates),
                protein = String.format("%.2f", it.protein),
                sugar = it.sugar?.let { sugar -> String.format("%.2f", sugar) },
            )
        }
    }
}