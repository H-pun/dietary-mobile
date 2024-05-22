package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.Food
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
    bottomContent: @Composable LazyItemScope.() -> Unit = {},
    feedback: List<String> = listOf()
) {
    val locale = remember {
        Locale.getDefault()
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        item {
            Text(
                text = stringResource(id = R.string.your_nutrition_today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            UserNutritionCard(
                totalCaloriesToday = totalCaloriesToday,
                totalCarbohydrateToday = totalCarbohydrateToday,
                totalProteinToday = totalProteinToday,
                totalFatToday = totalFatToday,
                maxDailyCalories = maxDailyCalories,
                maxDailyCarbohydrate = maxDailyCarbohydrate,
                maxDailyProtein = maxDailyProtein,
                maxDailyFat = maxDailyFat
            )
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
                    text = stringResource(R.string.total_carbo),
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
                    text = stringResource(R.string.total_protein),
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
                    text = stringResource(R.string.total_fat),
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

