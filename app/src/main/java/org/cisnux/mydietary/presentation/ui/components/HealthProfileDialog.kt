package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme

@Preview
@Composable
private fun HealthProfileDialogPreview() {
    DietaryTheme {
        HealthProfileDialog(
            isDialogOpen = true, onUpdate = {}, onDone = {}, onDismissRequest = {},
            age = "40",
            weight = "50",
            height = "170",
            gender = "Man",
        )
    }
}

@Composable
fun HealthProfileDialog(
    age: String,
    weight: String,
    height: String,
    gender: String,
    onUpdate: () -> Unit,
    onDone: () -> Unit,
    onDismissRequest: () -> Unit,
    isDialogOpen: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isDialogOpen) Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
        ) {
            Column(
                modifier = modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.health_profile_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.health_profile_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                ListTileProfile(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_age_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.age_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    bodyLabel = {
                        Text(
                            text = "$age years old",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                ListTileProfile(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_height_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.height_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    bodyLabel = {
                        Text(
                            text = "$height cm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ListTileProfile(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_scale_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.weight_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    bodyLabel = {
                        Text(
                            text = "$weight kg",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ListTileProfile(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_male_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.gender_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    bodyLabel = {
                        Text(
                            text = gender,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onUpdate) {
                        Text(
                            text = stringResource(id = R.string.health_profile_dialog_negative_btn),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDone) {
                        Text(
                            text = stringResource(id = R.string.health_profile_dialog_positive_btn),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}