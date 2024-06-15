package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.navigation.NavigationItem
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.commons.utils.AppDestination

@Preview
@Composable
private fun BottomBarPreview() {
    DietaryTheme {
        BottomBar(
            currentRoute = AppDestination.HomeRoute,
            onSelectedDestination = { _, _ -> },
        )
    }
}

@Composable
fun BottomBar(
    currentRoute: AppDestination,
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    navigationItems: List<NavigationItem<*>> = listOf(
        NavigationItem(
            title = stringResource(id = R.string.home_title),
            icon = Icons.Rounded.Home,
            destination = AppDestination.HomeRoute,
            contentDescription = stringResource(id = R.string.home_title)
        ),
        NavigationItem(
            title = stringResource(id = R.string.report_title),
            icon = painterResource(id = R.drawable.ic_report_24dp),
            destination = AppDestination.ReportRoute,
            contentDescription = stringResource(id = R.string.report_title)
        ),
        NavigationItem(
            title = stringResource(id = R.string.my_profile_title),
            icon = Icons.Rounded.AccountCircle,
            destination = AppDestination.MyProfileRoute,
            contentDescription = stringResource(id = R.string.my_profile_title)
        ),
    ),
) {
    NavigationBar(modifier = modifier) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                label = {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clearAndSetSemantics {}
                    )
                },
                icon = {
                    when (item.icon) {
                        is ImageVector -> Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )

                        is Painter ->
                            Icon(
                                painter = item.icon,
                                contentDescription = item.title
                            )
                    }

                },
                selected = currentRoute.route == item.destination.route,
                onClick = {
                    onSelectedDestination(item.destination, currentRoute)
                },
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = item.contentDescription
                }
            )
        }
    }
}