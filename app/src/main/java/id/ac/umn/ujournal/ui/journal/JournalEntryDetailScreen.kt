package id.ac.umn.ujournal.ui.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.util.HourTimeFormatter24
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import id.ac.umn.ujournal.ui.util.getAddressFromLatLong
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetailScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    journalEntryID: String?,
    onBackButtonClick: () -> Unit = {},
    onEditButtonClick: (String) -> Unit
) {
    if (journalEntryID == null) {
        onBackButtonClick()
        return
    }

    val journalEntry = remember(journalEntryID) { journalEntryViewModel.getJournalEntry(journalEntryID) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (journalEntry == null) {
        onBackButtonClick()
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = { Text(text = "Journal Detail") },
                onBackButtonClick = onBackButtonClick,
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Delete journal entry"
                        )
                    }
                    Spacer(Modifier.padding(4.dp))
                    IconButton(onClick = {
                        onEditButtonClick(journalEntryID)
                    }) {
                        Icon(
                            Icons.Filled.Edit,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Edit journal entry"
                        )
                    }
                }
            )
        },
    ) { padding: PaddingValues ->
        Surface(
            Modifier.padding(padding).fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            ) {
                if (journalEntry.imageURI != null) {
                    AsyncImage(
                        model = journalEntry.imageURI,
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentDescription = "Journal Entry Photo",
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(10.dp)) {
                    if (journalEntry.latitude != null && journalEntry.longitude != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Location icon",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Column {
                                getAddressFromLatLong(
                                    useDeprecated = true,
                                    lat = journalEntry.latitude!!,
                                    lon = journalEntry.longitude!!,
                                    context = LocalContext.current
                                )?.let {
                                    Text(
                                        text = it.getAddressLine(0),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Spacer(Modifier.padding(2.dp))
                                }
                                Text(
                                    text = "%.4f, %.4f".format(journalEntry.latitude, journalEntry.longitude),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    Spacer(Modifier.padding(vertical = 6.dp))
                    Text(
                        text = journalEntry.createdAt.format(ddMMMMyyyyDateTimeFormatter) + ", " +
                                journalEntry.createdAt.format(HourTimeFormatter24),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.padding(vertical = 4.dp))
                    Text(
                        text = journalEntry.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.padding(vertical = 4.dp))
                    Text(
                        text = journalEntry.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Journal Entry") },
            text = { Text("Are you sure you want to delete this journal entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        journalEntryViewModel.remove(journalEntryID)
                        showDeleteDialog = false
                        onBackButtonClick()
                    }
                ) {
                    Text(
                        "Yes",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        "No",
                        color = MaterialTheme.colorScheme.scrim
                    )
                }
            }
        )
    }

}