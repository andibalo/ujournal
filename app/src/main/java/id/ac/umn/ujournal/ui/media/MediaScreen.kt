package id.ac.umn.ujournal.ui.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.components.common.ErrorScreen
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.viewmodel.UserState
import id.ac.umn.ujournal.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaScreen(
    userViewModel: UserViewModel = viewModel(),
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick : () -> Unit = {},
    onMediaItemClick : (journalEntryID: String) -> Unit = {},
) {

    val groupedByDateJournalEntries = journalEntryViewModel.getJournalEntriesGroupedByDate()
    val userState by userViewModel.userState.collectAsState()

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
                        Icon(Icons.Filled.AccountCircle, contentDescription = "User Profile")
                    }
                },
                showBackButton = false
            )
        },

    ) { innerPadding: PaddingValues ->
        Column (
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
        ) {
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
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            maxItemsInEachRow = 3
                        ) {
                            val remainder = journalEntries.size % 3
                            val placeholdersNeeded = if (remainder != 0) 3 - remainder else 0

                            for (journalEntry in journalEntries) {
                                AsyncImage(
                                    model = journalEntry.imageURI,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clickable {
                                            onMediaItemClick(journalEntry.id.toString())
                                        },
                                    contentDescription = "Journal Entry Photo",
                                    contentScale = ContentScale.Crop
                                )
                            }

                            repeat(placeholdersNeeded){
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                )
                            }
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
