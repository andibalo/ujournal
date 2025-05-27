package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.ac.umn.ujournal.data.model.JournalEntry

@Composable
fun JournalEntryList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    list: List<JournalEntry>,
    onJournalEntryClick : (journalEntryID: String) -> Unit = {},
    state : LazyListState = rememberLazyListState(),
    listTopContent: @Composable () -> Unit = {},
    listHeaderContent:  @Composable () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding
    ) {

        item {
            listHeaderContent()
        }

        items(
            items = list,
            key = { entry -> entry.id }
        ) { entry ->
            JournalEntryItem(
                title = entry.title,
                description = entry.description,
                imageURI = entry.imageURI,
                createdAt = entry.createdAt,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onJournalEntryClick(entry.id)
                    }
            )
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}