package id.ac.umn.ujournal.ui.components.common.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import id.ac.umn.ujournal.ui.navigation.CalendarDateDetail
import id.ac.umn.ujournal.ui.navigation.CreateJournalEntry
import id.ac.umn.ujournal.ui.navigation.EditJournalEntry
import id.ac.umn.ujournal.ui.navigation.Home
import id.ac.umn.ujournal.ui.navigation.JournalEntryDetail
import id.ac.umn.ujournal.ui.navigation.Login
import id.ac.umn.ujournal.ui.navigation.Map
import id.ac.umn.ujournal.ui.navigation.Profile
import id.ac.umn.ujournal.ui.navigation.Register
import id.ac.umn.ujournal.ui.navigation.uJournalAppScreens
import id.ac.umn.ujournal.ui.util.UJournalNavigationContentPosition
import id.ac.umn.ujournal.ui.util.hasRoute
import id.ac.umn.ujournal.ui.util.isCompact
import id.ac.umn.ujournal.viewmodel.AuthState
import kotlinx.coroutines.launch

enum class LayoutType {
    HEADER, CONTENT
}

class UJournalNavSuiteScope(
    val navSuiteType: NavigationSuiteType
)

@Composable
fun UJournalNavigationWrapper(
    currentDestination: NavDestination?,
    navController: NavHostController,
    content: @Composable UJournalNavSuiteScope.() -> Unit
) {
    var isNavigationVisible by rememberSaveable { (mutableStateOf(false)) }
    var isTopBarVisible by rememberSaveable { (mutableStateOf(false)) }

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }

    val navLayoutType = when {
        adaptiveInfo.windowPosture.isTabletop -> NavigationSuiteType.NavigationBar
        adaptiveInfo.windowSizeClass.isCompact() -> NavigationSuiteType.NavigationBar
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
                windowSize.width >= 1200.dp -> NavigationSuiteType.NavigationDrawer
        else -> NavigationSuiteType.NavigationRail
    }
    val navContentPosition = when (adaptiveInfo.windowSizeClass.windowHeightSizeClass) {
        WindowHeightSizeClass.COMPACT -> UJournalNavigationContentPosition.TOP
        WindowHeightSizeClass.MEDIUM,
        WindowHeightSizeClass.EXPANDED -> UJournalNavigationContentPosition.CENTER
        else -> UJournalNavigationContentPosition.TOP
    }

    // Change the variable to this and use home as a backup screen if this returns null
    val currentScreen = uJournalAppScreens.find {
        it.route == currentDestination?.route || it.routeWithArgs == currentDestination?.route
    } ?: Home

    isNavigationVisible = shouldShowNavigation(currentScreen.route, navLayoutType)
    isTopBarVisible = shouldShowTopAppBar(currentScreen.route)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    // Avoid opening the modal drawer when there is a permanent drawer or a bottom nav bar,
    // but always allow closing an open drawer.
    var gesturesEnabled =
        drawerState.isOpen || navLayoutType == NavigationSuiteType.NavigationRail

    if (currentDestination.hasRoute(Map.route) && !drawerState.isOpen) {
        gesturesEnabled = false
    }

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            ModalNavigationDrawerContent(
                currentDestination = currentDestination,
                navigationContentPosition = navContentPosition,
                navController = navController,
                onDrawerClicked = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                onAddJournalClick = {
                    navController.navigate(CreateJournalEntry.route)
                },
            )
        },
    ) {
        NavigationSuiteScaffoldLayout(
            layoutType = navLayoutType,
            navigationSuite = {
                when (navLayoutType) {
                    NavigationSuiteType.NavigationBar -> UJournalBottomNavigationBar(
                        currentDestination = currentDestination,
                        navController = navController,
                        isVisible = isNavigationVisible
                    )
                    NavigationSuiteType.NavigationRail -> UJournalNavigationRail(
                        currentDestination = currentDestination,
                        navController = navController,
                        isVisible = isNavigationVisible,
                        onDrawerClicked = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        },
                        onAddJournalClick = {
                            navController.navigate(CreateJournalEntry.route)
                        },
                    )
                    NavigationSuiteType.NavigationDrawer -> PermanentNavigationDrawerContent(
                        currentDestination = currentDestination,
                        navigationContentPosition = navContentPosition,
                        navController = navController,
                        onAddJournalClick = {
                            navController.navigate(CreateJournalEntry.route)
                        },
                    )
                }
            }
        ) {
            UJournalNavSuiteScope(navLayoutType).content()
        }
    }
}

fun shouldShowNavigation(route : String, navLayoutType: NavigationSuiteType) : Boolean {

    when (navLayoutType) {
        NavigationSuiteType.NavigationRail ->
            when (route) {
                Login.route -> {
                    return false
                }
                Register.route -> {
                    return false
                }
                else -> {
                    return true
                }
            }
        NavigationSuiteType.NavigationDrawer ->
            when (route) {
                Login.route -> {
                    return false
                }
                Register.route -> {
                    return false
                }
                else -> {
                    return true
                }
            }
        else ->
            when (route) {
            Profile.route -> {
                return false
            }
            Login.route -> {
                return false
            }
            Register.route -> {
                return false
            }
            CreateJournalEntry.route -> {
                return false
            }
            EditJournalEntry.route -> {
                return false
            }
            JournalEntryDetail.route -> {
                return false
            }
            CalendarDateDetail.route -> {
                return false
            }
            else -> {
                return true
            }
        }
    }
}

fun shouldShowTopAppBar(route : String) : Boolean {

    when (route) {
        Profile.route -> {
            return false
        }
        Login.route -> {
            return false
        }
        Register.route -> {
            return false
        }
        CreateJournalEntry.route -> {
            return false
        }
        JournalEntryDetail.route -> {
            return false
        }
        CalendarDateDetail.route -> {
            return false
        }
        else -> {
            return true
        }
    }
}
