package id.ac.umn.ujournal.ui.journal

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime

import java.util.UUID

class JournalEntryViewModel : ViewModel() {
    private val _journalEntries = getJournalEntryTestData().toMutableStateList()
    val journalEntries: List<JournalEntry>
        get() = _journalEntries


    fun addJournalEntry(entry: JournalEntry) {
        _journalEntries.add(0,entry)
    }

    fun getJournalEntry(journalEntryID: String?): JournalEntry {
        return _journalEntries.first { it.id.toString() == journalEntryID }
    }

    fun remove() {
        _journalEntries.remove(_journalEntries.get(0))
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