package id.ac.umn.ujournal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import id.ac.umn.ujournal.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

import java.util.UUID

class JournalEntryViewModel : ViewModel() {
    private val _journalEntries = MutableStateFlow(getJournalEntryTestData())

    val journalEntries: StateFlow<List<JournalEntry>> get() = _journalEntries

    fun addJournalEntry(entry: JournalEntry) {
        Log.d("JournalEntryViewModel", "Adding entry: $entry")

        val currentList = _journalEntries.value
        Log.d("JournalEntryViewModel", "Current List: $currentList")

        _journalEntries.update { currentEntries ->
            listOf(entry) + currentEntries
        }
    }

    fun getJournalEntry(journalEntryID: String): JournalEntry? {
        return _journalEntries.value.firstOrNull { it.id.toString() == journalEntryID }
    }

    fun remove(journalEntryID: String) {
        Log.d("JournalEntryViewModel", "Removing entry with ID: $journalEntryID")

        _journalEntries.update { currentEntries ->
            currentEntries.filter { it.id.toString() != journalEntryID }
        }
    }
}

fun createTestJournalEntry(
    id: UUID = UUID.randomUUID(),
    title: String = "Test Journal Entry",
    description: String = "This is a test journal entry.",
    picture: String = "https://picsum.photos/300",
    geotag: List<String> = listOf("40.7128° N", "74.0060° W"),
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now()
): JournalEntry {
    return JournalEntry(
        id = id,
        title = title,
        description = description,
        imageURI = picture,
        geotag = geotag,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun getJournalEntryTestData(): List<JournalEntry> {
    return List(10) { index ->
        createTestJournalEntry(
            title = "Test Journal Entry $index",
            description = "This is test journal entry number $index.",
            geotag = listOf("${40.7128 + index}° N", "${74.0060 + index}° W"),
            createdAt = LocalDateTime.now().minusDays(index.toLong())
        )
    }
}