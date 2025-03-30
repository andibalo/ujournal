package id.ac.umn.ujournal.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import id.ac.umn.ujournal.model.JournalEntry


@Composable
fun SearchBar(
    journalEntries: List<JournalEntry>,
    onFilteredEntriesChanged: (List<JournalEntry>) -> Unit,
    onCloseSearch: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    // Filtering logic inside SearchBar
    val filteredEntries by remember(searchText, journalEntries) {
        derivedStateOf {
            if (searchText.isBlank()) journalEntries
            else journalEntries.filter {
                it.title.contains(searchText, ignoreCase = true) ||
                        it.description.contains(searchText, ignoreCase = true)
            }
        }
    }

    // Update HomeScreen with filtered results
    LaunchedEffect(filteredEntries) {
        onFilteredEntriesChanged(filteredEntries)
    }

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
            keyboardActions = KeyboardActions(onDone = { onCloseSearch() }),
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
