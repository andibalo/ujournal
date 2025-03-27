package id.ac.umn.ujournal.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.ui.components.common.ErrorScreen
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.JournalEntryList
import id.ac.umn.ujournal.ui.components.journalentry.JournalEntryListHeader
import id.ac.umn.ujournal.ui.util.isScrollingUp
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.viewmodel.UserState
import id.ac.umn.ujournal.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel = viewModel(),
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick : () -> Unit = {},
    onFABClick : () -> Unit = {},
    onJournalEntryClick : (journalEntryID: String) -> Unit = {},
) {

    val journalEntries by journalEntryViewModel.journalEntries.collectAsState()
    val userState by userViewModel.userState.collectAsState()

    var isSortedDescending by remember { mutableStateOf(true) }
    val sortedEntries by remember {
        derivedStateOf {
            if (isSortedDescending) {
                journalEntries.sortedByDescending { it.createdAt }
            } else {
                journalEntries.sortedBy { it.createdAt }
            }
        }
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
    }

    if (userState == UserState.Loading) {
        LoadingScreen()
        return
    }

    if (userState is UserState.Error) {
        ErrorScreen()
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    val user = (userState as UserState.Success).user
                    Text(
                        text = "Hello, " + user.firstName
                    )
                },
                actions = {
                    // TODO: implement search
                    IconButton(
                        onClick = { /* Do something */ }
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "User Profile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                showBackButton = false
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = lazyListState.isScrollingUp(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                FloatingActionButton (
                    onClick = onFABClick,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Filled.Add, "Add new journal entry floating action button",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding: PaddingValues ->
        Surface (
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            JournalEntryList(
                list = sortedEntries,
                modifier = Modifier.fillMaxSize(),
                onJournalEntryClick = onJournalEntryClick,
                state = lazyListState,
                contentPadding = PaddingValues(8.dp),
                listTopContent = {
                    // TODO: add content to card
                    Card(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                    ) {
                        Box(
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary,
                                        )
                                    )
                                ),
                        ){
                            Text(text = "Some content", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    Spacer(Modifier.padding(bottom = 8.dp))
                },
                listHeaderContent = {
                    JournalEntryListHeader(
                        modifier = Modifier.fillMaxWidth(),
                        onToggleSort = {
                            isSortedDescending = !isSortedDescending
                        }
                    )
                },
            )
        }
    }
}