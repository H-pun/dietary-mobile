package org.cisnux.mydietary.presentation.ui.components

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchBarState(
    val query: String,
    val active: Boolean,
    val isSearch: Boolean,
) : Parcelable

@Composable
fun rememberSearchBarState(
    initialQuery: String,
    initialActive: Boolean,
    initialSearch: Boolean
): MutableState<SearchBarState> =
    rememberSaveable {
        mutableStateOf(
            SearchBarState(
                query = initialQuery,
                active = initialActive,
                isSearch = initialSearch
            )
        )
    }