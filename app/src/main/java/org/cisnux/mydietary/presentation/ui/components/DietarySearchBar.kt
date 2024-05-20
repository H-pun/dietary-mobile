package org.cisnux.mydietary.presentation.ui.components

import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.Keyword

@Preview(showBackground = true)
@Composable
private fun DietarySearchBarPreview() {
    var searchBarState by rememberSearchBarState(
        initialQuery = "", initialActive = false, initialSearch = true
    )

    DietaryTheme {
        DietarySearchBar(
            query = searchBarState.query,
            onQueryChange = { searchBarState = searchBarState.copy(query = it) },
            active = searchBarState.active,
            onActiveChange = { searchBarState = searchBarState.copy(active = it) },
            keywordSuggestions = listOf(),
            navigateUp = {},
            isSearch = searchBarState.isSearch,
            onSearchChange = { _, _ -> })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DietarySearchBar(
    query: String,
    onQueryChange: (query: String) -> Unit,
    active: Boolean,
    onSearchChange: (query: String, isSearch: Boolean) -> Unit,
    onActiveChange: (active: Boolean) -> Unit,
    keywordSuggestions: List<Keyword>,
    isSearch: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .semantics {
                isTraversalGroup = true
            }
            .zIndex(1f)
            .fillMaxWidth()
    ) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                onSearchChange(it, true)
            },
            active = active,
            onActiveChange = onActiveChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.searchbar_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                if (active or isSearch) {
                    IconButton(
                        onClick =
                        navigateUp
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                } else menu()
            },
            trailingIcon = {
                IconButton(onClick = { onSearchChange(query, true) }, enabled = query.isNotBlank()) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            modifier = modifier.align(Alignment.TopCenter),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(keywordSuggestions, key = { it.id }, contentType = { it }) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        modifier = Modifier
                            .animateItemPlacement()
                            .clickable {
                                onSearchChange(it.text, true)
                            }
                    )
                }
            }
        }
    }
}
