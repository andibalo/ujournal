package id.ac.umn.ujournal.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.ui.components.common.ErrorScreen
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.*
import id.ac.umn.ujournal.ui.util.isScrollingUp
import id.ac.umn.ujournal.viewmodel.*
import id.ac.umn.ujournal.ui.components.common.SearchBar
import id.ac.umn.ujournal.ui.util.isCompact

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
    val user by userViewModel.user.collectAsState()
    var err by remember { mutableStateOf("") }
    var isSortedDescending by remember { mutableStateOf(true) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val sortedEntries by remember(journalEntries, isSortedDescending) {
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

    val adaptiveInfo = currentWindowAdaptiveInfo()

    LaunchedEffect(Unit) {
        isLoading = true

        try {
            val u = userViewModel.loadUserData()
            journalEntryViewModel.getJournalEntries(u.id)
        } catch (e: Exception) {
            err = e.message ?: "Something went wrong"
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    if (err != "") {
        ErrorScreen()
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    AnimatedContent(
                        targetState = isSearchActive,
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(220)
                            ) togetherWith fadeOut(animationSpec = tween(90))
                        }
                    ) { targetState ->
                        if (targetState) {
                            SearchBar(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onCloseSearch = { isSearchActive = false },
                                onClearSearch = {
                                    searchText = ""
                                },
                                value = searchText,
                                onTextChange = {
                                    searchText = it
                                },
                                placeholder = {
                                    Text("Search journals...")
                                }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                            ) {
                                Text(text = "Hello, " + (user?.firstName ?: ""))
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isSearchActive) {
                            isSearchActive = false
                            searchText = ""
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
            if(adaptiveInfo.windowSizeClass.isCompact()) {
                AnimatedVisibility(
                    visible = lazyListState.isScrollingUp(),
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    FloatingActionButton(
                        onClick = onFABClick,
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            "Add new journal entry floating action button",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding: PaddingValues ->
        Surface(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                JournalEntryListSummary(
                    modifier = Modifier.fillMaxWidth().padding(8.dp, 8.dp, 8.dp,0.dp),
                    entriesCreated = journalEntries.size,
                    dailyStreak = journalEntries.size
                )
                if (filteredEntries.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "No Journal Entries Yet",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                } else {
                    JournalEntryList(
                        list = filteredEntries,
                        modifier = Modifier.fillMaxSize(),
                        onJournalEntryClick = onJournalEntryClick,
                        state = lazyListState,
                        contentPadding = PaddingValues(8.dp),
                        listHeaderContent = {
                            JournalEntryListHeader(
                                modifier = Modifier.fillMaxWidth(),
                                onToggleSort = {
                                    isSortedDescending = !isSortedDescending
                                },
                                isSortedDescending = isSortedDescending
                            )
                        },
                    )
                }
            }
        }
    }
}