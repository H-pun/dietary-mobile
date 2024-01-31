package dev.cisnux.dietary.presentation.adddiary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme

@Composable
fun AddDiaryScreen(
    onNavigateUp: () -> Unit,
    navigateToFoodScanner: (String, String) -> Unit
) {
    val foodDiaryCategories = stringArrayResource(id = R.array.food_diary_category)
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var selectedFoodDiaryCategory by rememberSaveable {
        mutableStateOf(foodDiaryCategories[0])
    }

    AddDiaryContent(
        body = {
            AddDiaryBody(
                title = title,
                selectedFoodDiaryCategory = selectedFoodDiaryCategory,
                onTitleChange = { newValue -> title = newValue },
                onFoodDiaryCategoryChange = { newValue ->
                    selectedFoodDiaryCategory = newValue
                },
                foodDiaryCategories = foodDiaryCategories,
                onSave = { navigateToFoodScanner(title, selectedFoodDiaryCategory) },
                modifier = Modifier.padding(it)
            )
        },
        onNavigateUp = onNavigateUp
    )
}

@Preview(showBackground = true)
@Composable
private fun AddDiaryContentPreview() {
    val foodDiaryCategories = stringArrayResource(id = R.array.food_diary_category)
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var selectedFoodDiaryCategory by rememberSaveable {
        mutableStateOf(foodDiaryCategories[0])
    }

    DietaryTheme {
        AddDiaryContent(
            body = {
                AddDiaryBody(
                    title = title,
                    selectedFoodDiaryCategory = selectedFoodDiaryCategory,
                    onTitleChange = { newValue -> title = newValue },
                    onFoodDiaryCategoryChange = { newValue ->
                        selectedFoodDiaryCategory = newValue
                    },
                    foodDiaryCategories = foodDiaryCategories,
                    onSave = { /*TODO*/ },
                    modifier = Modifier.padding(it)
                )
            },
            onNavigateUp = { /*TODO*/ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDiaryBody(
    title: String,
    selectedFoodDiaryCategory: String,
    onTitleChange: (String) -> Unit,
    onFoodDiaryCategoryChange: (String) -> Unit,
    foodDiaryCategories: Array<String>,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTitleFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isFoodDiaryCategoryExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fastfood_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = title,
            singleLine = true,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.title_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (title.isNotEmpty() and title.isBlank())
                    Text(
                        text = stringResource(R.string.title_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isTitleFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = title.isNotEmpty() and title.isBlank(),
            trailingIcon = {
                if (title.isNotEmpty() and title.isBlank())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isTitleFocused = it.isFocused
                },
        )
        ExposedDropdownMenuBox(
            expanded = isFoodDiaryCategoryExpanded,
            onExpandedChange = { isFoodDiaryCategoryExpanded = it },
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = when (selectedFoodDiaryCategory) {
                                foodDiaryCategories[0] -> R.drawable.ic_breakfast_24dp
                                foodDiaryCategories[1] -> R.drawable.ic_lunch_24dp
                                else -> R.drawable.ic_dinner_24dp
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                value = selectedFoodDiaryCategory,
                onValueChange = {},
                label = {
                    Text(
                        text = "Category",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFoodDiaryCategoryExpanded) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = isFoodDiaryCategoryExpanded,
                onDismissRequest = { isFoodDiaryCategoryExpanded = false },
            ) {
                foodDiaryCategories.forEach { selectedOption ->
                    DropdownMenuItem(
                        text = { Text(selectedOption) },
                        onClick = {
                            onFoodDiaryCategoryChange(selectedOption)
                            isFoodDiaryCategoryExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = title.isNotBlank(),
        ) {
            Text(text = "Add")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiaryContent(
    body: @Composable (PaddingValues) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Food Diary",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = modifier,
    ) {
        body(it)
    }
}
