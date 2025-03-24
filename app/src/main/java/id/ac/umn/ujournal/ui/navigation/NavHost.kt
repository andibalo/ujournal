package id.ac.umn.ujournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.ac.umn.ujournal.ui.auth.LoginScreen
import id.ac.umn.ujournal.ui.auth.RegisterScreen
import id.ac.umn.ujournal.ui.calendar.CalendarScreen
import id.ac.umn.ujournal.ui.home.HomeScreen
import id.ac.umn.ujournal.ui.journal.CreateJournalEntryScreen
import id.ac.umn.ujournal.ui.journal.JournalEntryDetailScreen
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.ui.map.MapScreen
import id.ac.umn.ujournal.ui.map.MediaScreen
import id.ac.umn.ujournal.ui.profile.ProfileScreen
import id.ac.umn.ujournal.viewmodel.UserViewModel

@Composable
fun UJournalNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val journalEntryViewModel: JournalEntryViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

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
                navigateToHomeScreen = {
                    navController.navigateSingleTopTo(Home.route)
                }
            )
        }
        composable(route = Register.route) {
            RegisterScreen(
                userViewModel = userViewModel,
                onLoginClick = {
                    navController.navigateSingleTopTo(Login.route)
                },
                navigateToHomeScreen = {
                    navController.navigateSingleTopTo(Home.route)
                }
            )
        }
        composable(route = Home.route) {
            HomeScreen(
                userViewModel = userViewModel,
                journalEntryViewModel = journalEntryViewModel,
                onProfileClick = {
                    navController.navigateSingleTopTo(Profile.route)
                },
                onFABClick = {
                    navController.navigateSingleTopTo(CreateJournalEntry.route)
                },
                onJournalEntryClick = { journalEntryID ->
                    println("Journal Entry ID: $journalEntryID")
                    navController.navigateSingleTopTo("${JournalEntryDetail.route}/$journalEntryID")
                }
            )
        }
        composable(route = Calendar.route) {
            CalendarScreen(
                journalEntryViewModel = journalEntryViewModel,
                onProfileClick = {
                    navController.navigateSingleTopTo(Profile.route)
                },
            )
        }
        composable(route = Media.route) {
            MediaScreen(
                journalEntryViewModel = journalEntryViewModel,
                onProfileClick = {
                    navController.navigateSingleTopTo(Profile.route)
                },
            )
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
                journalEntryViewModel = journalEntryViewModel,
                onBackButtonClick = {
                    if (navController.previousBackStackEntry != null) {
                        navController.navigateUp()
                    }
                },
            )
        }
        composable(
            route = JournalEntryDetail.routeWithArgs,
            arguments =  JournalEntryDetail.arguments
        ) { navBackStackEntry ->

            val journalEntryID =
                navBackStackEntry.arguments?.getString(JournalEntryDetail.journalEntryIDArg)

            JournalEntryDetailScreen(
                journalEntryViewModel = journalEntryViewModel,
                journalEntryID = journalEntryID,
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
