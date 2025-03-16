package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime

import java.util.UUID

class JournalEntryViewModel : ViewModel() {
    private val _journalEntries = getJournalEntryTestData().toMutableStateList()
    val journalEntries: List<JournalEntry>
        get() = _journalEntries


    fun remove(item: JournalEntry) {
        _journalEntries.remove(item)
    }
}

fun createTestJournalEntry(
    id: UUID = UUID.randomUUID(),
    title: String = "Test Journal Entry",
    description: String = "This is a test journal entry.",
    picture: String = "https://example.com/test_picture.jpg",
    geotag: List<String> = listOf("40.7128° N", "74.0060° W"),
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now()
): JournalEntry {
    return JournalEntry(
        id = id,
        title = title,
        description = description,
        picture = picture,
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