package id.ac.umn.ujournal.ui.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.components.common.ErrorScreen
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.NonLazyGrid
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.viewmodel.UserState
import id.ac.umn.ujournal.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    userViewModel: UserViewModel = viewModel(),
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick : () -> Unit = {},
    onMediaItemClick : (journalEntryID: String) -> Unit = {},
) {

    val groupedByDateJournalEntries = journalEntryViewModel.getJournalEntriesGroupedByDate(true)
    val userState by userViewModel.userState.collectAsState()

    val adaptiveInfo = currentWindowAdaptiveInfo()

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

    ) { innerPadding: PaddingValues ->
        Column (
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
        ) {
            if (groupedByDateJournalEntries.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No Journal Entries With Media",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            } else {
                LazyColumn {
                    groupedByDateJournalEntries.forEach { (date, journalEntries) ->

                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        item {
                            NonLazyGrid(
                                columns = when(
                                    adaptiveInfo.windowSizeClass.windowWidthSizeClass
                                ) {
                                    WindowWidthSizeClass.MEDIUM -> 5
                                    WindowWidthSizeClass.EXPANDED -> 7
                                    else -> 3
                                },
                                itemCount = journalEntries.size,
                                modifier = Modifier
                                    .padding(start = 7.5.dp, end = 7.5.dp)
                            ) {
                                AsyncImage(
                                    model = journalEntries[it].imageURI,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clickable {
                                            onMediaItemClick(journalEntries[it].id.toString())
                                        },
                                    contentDescription = "Journal Entry Photo",
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(
                                modifier = Modifier.padding(8.dp).fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
