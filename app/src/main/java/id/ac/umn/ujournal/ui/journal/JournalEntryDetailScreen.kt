package id.ac.umn.ujournal.ui.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetailScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    journalEntryID: String?,
    onBackButtonClick : () -> Unit = {},
) {

    if (journalEntryID == null) {
        onBackButtonClick()
        return
    }

    val journalEntry = remember(journalEntryID) { journalEntryViewModel.getJournalEntry(journalEntryID) }

    if (journalEntry == null) {
        onBackButtonClick()
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    Text(
                        text = "Journal Detail",
                    )
                },
                onBackButtonClick = onBackButtonClick,
                actions = {
                    IconButton(
                        onClick = {
                            // TODO: add delete functionality
                        }
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Delete journal entry"
                        )
                    }
                    Spacer(Modifier.padding(4.dp))
                    IconButton(
                        onClick = {
                            // TODO: add edit functionality
                        }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit journal entry")
                    }
                }
            )
        },
    ) { padding: PaddingValues ->
        Column(
            modifier =
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ,
        ) {
            if(journalEntry.imageURI != null){
                AsyncImage(
                    model = journalEntry.imageURI,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentDescription = "Journal Entry Photo",
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = journalEntry.createdAt.format(ddMMMMyyyyDateTimeFormatter),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.padding(vertical = 6.dp))
                Text(
                    text = journalEntry.title,
                    style = MaterialTheme.typography.titleLarge
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