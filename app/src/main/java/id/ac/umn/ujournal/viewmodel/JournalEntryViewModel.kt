package id.ac.umn.ujournal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import id.ac.umn.ujournal.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

    fun getJournalEntriesGroupedByDate(): Map<String, List<JournalEntry>> {
        val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        return _journalEntries.value
            .sortedByDescending { it.createdAt }
            .groupBy { entry ->
                val date = entry.createdAt.toLocalDate()
                dateFormatter.format(date)
            }
    }

}

fun createTestJournalEntry(
    id: UUID = UUID.randomUUID(),
    title: String = "Test Journal Entry",
    description: String = "This is a test journal entry.",
    picture: String = "https://picsum.photos/300",
    latitude: Double = 40.7128,
    longitude: Double = -74.0060,
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now()
): JournalEntry {
    return JournalEntry(
        id = id,
        title = title,
        description = description,
        imageURI = picture,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun getJournalEntryTestData(): List<JournalEntry> {
    return List(10) { index ->
        createTestJournalEntry(
            title = "Test Journal Entry $index",
            description = "This is test journal entry number $index.",
            latitude = 40.7128,
            longitude = 74.0060,
            createdAt = LocalDateTime.now().minusDays(index.toLong())
        )
    }
}