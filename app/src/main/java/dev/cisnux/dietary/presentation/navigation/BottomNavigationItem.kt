package dev.cisnux.dietary.presentation.navigation

import androidx.compose.runtime.Immutable
import dev.cisnux.dietary.utils.AppDestination

@Immutable
data class BottomNavigationItem<out T>(
    val icon: T,
    val title: String,
    val destination: AppDestination,
    val contentDescription: String,
)