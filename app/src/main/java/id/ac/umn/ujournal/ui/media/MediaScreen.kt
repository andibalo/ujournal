package id.ac.umn.ujournal.ui.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.components.UJournalTopAppBar
import id.ac.umn.ujournal.ui.journal.JournalEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick : () -> Unit = {},
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    // TODO:  make dynamic
                    Text(
                        text = "Hello, Andi",
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

    ) { padding: PaddingValues ->
        Surface {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                Text(
                    text = "17 Maret 2025",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(10.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3)
                ) {
                    items(journalEntryViewModel.journalEntries.size) { index ->
                        AsyncImage(
                            model = journalEntryViewModel.journalEntries.get(index).imageURI,
                            modifier = Modifier.height(100.dp).fillMaxWidth(),
                            contentDescription = "Journal Entry Photo",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}