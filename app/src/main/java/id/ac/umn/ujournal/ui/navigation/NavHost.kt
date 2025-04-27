package id.ac.umn.ujournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.ac.umn.ujournal.ui.auth.LoginScreen
import id.ac.umn.ujournal.ui.auth.RegisterScreen
import id.ac.umn.ujournal.ui.calendar.CalendarDataDetailScreen
import id.ac.umn.ujournal.ui.calendar.CalendarScreen
import id.ac.umn.ujournal.ui.components.common.ProtectedScreen
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarControllerProvider
import id.ac.umn.ujournal.ui.home.HomeScreen
import id.ac.umn.ujournal.ui.journal.CreateJournalEntryScreen
import id.ac.umn.ujournal.ui.journal.JournalEntryDetailScreen
import id.ac.umn.ujournal.ui.journal.EditJournalEntryScreen
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.ui.map.MapScreen
import id.ac.umn.ujournal.ui.map.MediaScreen
import id.ac.umn.ujournal.ui.profile.ProfileScreen
import id.ac.umn.ujournal.viewmodel.AuthViewModel
import id.ac.umn.ujournal.viewmodel.ThemeViewModel
import id.ac.umn.ujournal.viewmodel.UserViewModel

@Composable
fun UJournalNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel,
    authViewModel: AuthViewModel,
    journalEntryViewModel: JournalEntryViewModel,
    userViewModel: UserViewModel
) {

    SnackbarControllerProvider { snackBarHost ->
        NavHost(
            navController = navController,
            startDestination = Login.route,
            modifier = modifier
        ) {
            composable(route = Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
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
                    authViewModel = authViewModel,
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
                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
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
            }
            composable(route = Calendar.route) {
                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
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
            }
            composable(route = Media.route) {
                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
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
            }
            composable(route = Map.route) {
                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
                    MapScreen(
                        journalEntryViewModel = journalEntryViewModel,
                        navigateToJournalDetail = { journalEntry ->
                            navController.navigate("${JournalEntryDetail.route}/${journalEntry.id}")
                        }
                    )
                }
            }
            composable(route = Profile.route) {
                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ){
                    ProfileScreen(
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel,
                        userViewModel = userViewModel,
                        onBackButtonClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            }
                        },
                    )
                }
            }
            composable(route = CreateJournalEntry.route) {
                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
                    CreateJournalEntryScreen(
                        journalEntryViewModel = journalEntryViewModel,
                        onBackButtonClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            }
                        },
                    )
                }
            }
            composable(
                route = "${EditJournalEntry.route}/{journalEntryID}",
            ) { navBackStackEntry ->

                val journalEntryID =
                    navBackStackEntry.arguments?.getString("journalEntryID")

                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
                    EditJournalEntryScreen(
                        journalEntryID = journalEntryID,
                        journalEntryViewModel = journalEntryViewModel,
                        onBackButtonClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            }
                        }
                    )
                }
            }
            composable(
                route = JournalEntryDetail.routeWithArgs,
                arguments =  JournalEntryDetail.arguments
            ) { navBackStackEntry ->

                val journalEntryID =
                    navBackStackEntry.arguments?.getString(JournalEntryDetail.journalEntryIDArg)

                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
                    JournalEntryDetailScreen(
                        journalEntryViewModel = journalEntryViewModel,
                        journalEntryID = journalEntryID,
                        onBackButtonClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            }
                        },
                        onEditButtonClick = { journalEntryID ->
                            navController.navigate("${EditJournalEntry.route}/$journalEntryID")
                        }
                    )
                }
            }
            composable(
                route = CalendarDateDetail.routeWithArgs,
                arguments =  CalendarDateDetail.arguments
            ) { navBackStackEntry ->

                val dateArg =
                    navBackStackEntry.arguments?.getString(CalendarDateDetail.calendarDateArg)

                ProtectedScreen(
                    redirectToLogin = {
                        navController.navigate(Login.route)
                    },
                    authViewModel = authViewModel
                ) {
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
}