package id.ac.umn.ujournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.ac.umn.ujournal.ui.calendar.CalendarScreen
import id.ac.umn.ujournal.ui.home.HomeScreen
import id.ac.umn.ujournal.ui.map.MapScreen
import id.ac.umn.ujournal.ui.map.MediaScreen

@Composable
fun UJournalNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreen()
        }
        composable(route = Calendar.route) {
            CalendarScreen()
        }
        composable(route = Media.route) {
            MediaScreen()
        }
        composable(route = Map.route) {
            MapScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
