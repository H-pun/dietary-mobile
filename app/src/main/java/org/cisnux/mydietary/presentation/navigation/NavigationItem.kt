package org.cisnux.mydietary.presentation.navigation

import androidx.compose.runtime.Immutable
import org.cisnux.mydietary.utils.AppDestination

@Immutable
data class NavigationItem<out T>(
    val icon: T,
    val title: String,
    val destination: AppDestination,
    val contentDescription: String,
)