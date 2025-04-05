package id.ac.umn.ujournal.ui.components.common.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import id.ac.umn.ujournal.ui.navigation.TOP_LEVEL_DESTINATIONS
import id.ac.umn.ujournal.ui.util.hasRoute

@Composable
fun UJournalBottomNavigationBar(
    currentDestination: NavDestination?,
    navController: NavHostController,
    isVisible: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically() ,
    ) {
        NavigationBar {
            TOP_LEVEL_DESTINATIONS.forEachIndexed { index, destination ->
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = { destination.icon?.let { Icon(it, contentDescription = destination.route) } },
                    label = { Text(destination.name) },
                    selected = currentDestination.hasRoute(destination.route),
                    onClick = {
                        navController.navigate(destination.route)
                    }
                )
            }
        }
    }
}