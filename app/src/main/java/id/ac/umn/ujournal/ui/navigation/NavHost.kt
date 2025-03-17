package id.ac.umn.ujournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.ac.umn.ujournal.ui.auth.LoginScreen
import id.ac.umn.ujournal.ui.auth.RegisterScreen
import id.ac.umn.ujournal.ui.calendar.CalendarScreen
import id.ac.umn.ujournal.ui.home.HomeScreen
import id.ac.umn.ujournal.ui.journal.CreateJournalEntryScreen
import id.ac.umn.ujournal.ui.map.MapScreen
import id.ac.umn.ujournal.ui.map.MediaScreen
import id.ac.umn.ujournal.ui.profile.ProfileScreen

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
        composable(route = Login.route) {
            LoginScreen(
                onSignUpClick = {
                    navController.navigateSingleTopTo(Register.route)
                },
            )
        }
        composable(route = Register.route) {
            RegisterScreen(
                onLoginClick = {
                    navController.navigateSingleTopTo(Login.route)
                },
            )
        }
        composable(route = Home.route) {
            HomeScreen(
                onProfileClick = {
                    navController.navigateSingleTopTo(Profile.route)
                },
                onFABClick = {
                    navController.navigateSingleTopTo(CreateJournalEntry.route)
                },
            )
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
        composable(route = Profile.route) {
            ProfileScreen(
                onBackButtonClick = {
                    if (navController.previousBackStackEntry != null) {
                        navController.navigateUp()
                    }
                },
                onLogoutButtonClick = {
                    navController.navigateSingleTopTo(Login.route)
                }
            )
        }
        composable(route = CreateJournalEntry.route) {
            CreateJournalEntryScreen(
                onBackButtonClick = {
                    if (navController.previousBackStackEntry != null) {
                        navController.navigateUp()
                    }
                },
            )
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
