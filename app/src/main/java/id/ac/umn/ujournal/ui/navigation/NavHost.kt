package id.ac.umn.ujournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.ac.umn.ujournal.ui.auth.LoginScreen
import id.ac.umn.ujournal.ui.auth.RegisterScreen
import id.ac.umn.ujournal.ui.calendar.CalendarDataDetailScreen
import id.ac.umn.ujournal.ui.calendar.CalendarScreen
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarControllerProvider
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

    SnackbarControllerProvider { snackBarHost ->
        NavHost(
            navController = navController,
            startDestination = Home.route,
            modifier = modifier
        ) {
            composable(route = Login.route) {
                LoginScreen(
                    userViewModel = userViewModel,
                    onSignUpClick = {
                        navController.navigate(Register.route)
                    },
                    navigateToHomeScreen = {
                        navController.navigate(Home.route)
                    },
                    snackbarHostState = snackBarHost,
                )
            }
            composable(route = Register.route) {
                RegisterScreen(
                    userViewModel = userViewModel,
                    onLoginClick = {
                        navController.navigate(Login.route)
                    },
                    navigateToHomeScreen = {
                        navController.navigate(Home.route)
                    },
                    snackbarHostState = snackBarHost,
                )
            }
            composable(route = Home.route) {
                HomeScreen(
                    userViewModel = userViewModel,
                    journalEntryViewModel = journalEntryViewModel,
                    onProfileClick = {
                        navController.navigate(Profile.route)
                    },
                    onFABClick = {
                        navController.navigate(CreateJournalEntry.route)
                    },
                    onJournalEntryClick = { journalEntryID ->
                        navController.navigate("${JournalEntryDetail.route}/$journalEntryID")
                    }
                )
            }
            composable(route = Calendar.route) {
                CalendarScreen(
                    userViewModel = userViewModel,
                    journalEntryViewModel = journalEntryViewModel,
                    onProfileClick = {
                        navController.navigate(Profile.route)
                    },
                    navigateToCalendarDateDetailScreen = { date ->
                        navController.navigate("${CalendarDateDetail.route}/$date")
                    }
                )
            }
            composable(route = Media.route) {
                MediaScreen(
                    userViewModel = userViewModel,
                    journalEntryViewModel = journalEntryViewModel,
                    onProfileClick = {
                        navController.navigate(Profile.route)
                    },
                    onMediaItemClick = { journalEntryID ->
                        navController.navigate("${JournalEntryDetail.route}/$journalEntryID")
                    },
                )
            }
            composable(route = Map.route) {
                MapScreen(
                    journalEntryViewModel = journalEntryViewModel,
                    navigateToJournalDetail = { journalEntry ->
                        navController.navigate("${JournalEntryDetail.route}/${journalEntry.id}")
                    }
                )
            }
            composable(route = Profile.route) {
                ProfileScreen(
                    userViewModel = userViewModel,
                    onBackButtonClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.navigateUp()
                        }
                    },
                    navigateToLoginScreen = {
                        navController.navigate(Login.route)
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
            composable(
                route = CalendarDateDetail.routeWithArgs,
                arguments =  CalendarDateDetail.arguments
            ) { navBackStackEntry ->

                val dateArg =
                    navBackStackEntry.arguments?.getString(CalendarDateDetail.calendarDateArg)

                CalendarDataDetailScreen(
                    selectedDate = dateArg,
                    journalEntryViewModel = journalEntryViewModel,
                    onBackButtonClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.navigateUp()
                        }
                    },
                    onJournalEntryClick = { journalEntryID ->
                        navController.navigate("${JournalEntryDetail.route}/$journalEntryID")
                    }
                )
            }
        }
    }
}