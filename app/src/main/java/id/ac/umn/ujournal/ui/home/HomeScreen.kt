package id.ac.umn.ujournal.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.ui.components.common.ErrorScreen
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.*
import id.ac.umn.ujournal.ui.util.isScrollingUp
import id.ac.umn.ujournal.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel = viewModel(),
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick: () -> Unit = {},
    onFABClick: () -> Unit = {},
    onJournalEntryClick: (journalEntryID: String) -> Unit = {},
) {
    val journalEntries by journalEntryViewModel.journalEntries.collectAsState()
    val userState by userViewModel.userState.collectAsState()
    var isSortedDescending by remember { mutableStateOf(true) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val sortedEntries by remember {
        derivedStateOf {
            if (isSortedDescending) {
                journalEntries.sortedByDescending { it.createdAt }
            } else {
                journalEntries.sortedBy { it.createdAt }
            }
        }
    }

    val filteredEntries by remember {
        derivedStateOf {
            if (searchText.isBlank()) sortedEntries
            else sortedEntries.filter {
                it.title.contains(searchText, ignoreCase = true) ||
                        it.description.contains(searchText, ignoreCase = true)
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
                    AnimatedVisibility(
                        visible = !isSearchActive,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                    ) {
                        val user = (userState as UserState.Success).user
                        Text(text = "Hello, " + user.firstName)
                    }
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            TextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { isSearchActive = false }),
                                placeholder = { Text("Search journals...") },
                                trailingIcon = {
                                    if (searchText.isNotBlank()) {
                                        IconButton(onClick = { searchText = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isSearchActive) {
                            isSearchActive = false
                            searchText = "" // Clear search when closing
                        } else {
                            isSearchActive = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                            contentDescription = if (isSearchActive) "Close search" else "Search",
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
                FloatingActionButton(
                    onClick = onFABClick,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Filled.Add,
                        "Add new journal entry floating action button",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding: PaddingValues ->
        Surface(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            JournalEntryList(
                list = filteredEntries,
                modifier = Modifier.fillMaxSize(),
                onJournalEntryClick = onJournalEntryClick,
                state = lazyListState,
                contentPadding = PaddingValues(8.dp),
                listTopContent = {
                    JournalEntryListSummary(
                        modifier = Modifier.fillMaxWidth(),
                        entriesCreated = filteredEntries.size,
                        dailyStreak = filteredEntries.size
                    )
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
