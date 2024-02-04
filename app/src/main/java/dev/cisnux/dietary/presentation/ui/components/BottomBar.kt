package dev.cisnux.dietary.presentation.ui.components

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
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.navigation.BottomNavigationItem
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.utils.AppDestination

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
    navigationItems: List<BottomNavigationItem<*>> = listOf(
        BottomNavigationItem(
            title = stringResource(id = R.string.home_title),
            icon = Icons.Rounded.Home,
            destination = AppDestination.HomeRoute,
            contentDescription = stringResource(id = R.string.home_title)
        ),
        BottomNavigationItem(
            title = stringResource(id = R.string.report_title),
            icon = painterResource(id = R.drawable.ic_report_24dp),
            destination = AppDestination.ReportRoute,
            contentDescription = stringResource(id = R.string.report_title)
        ),
        BottomNavigationItem(
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
                        modifier = Modifier.clearAndSetSemantics {})
                },
                icon = {
                    when (item.icon) {
                        is ImageVector -> Icon(
                            imageVector = item.icon,
                            contentDescription = null
                        )

                        is Painter ->
                            Icon(
                                painter = item.icon,
                                contentDescription = null
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