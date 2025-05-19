package id.ac.umn.ujournal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import id.ac.umn.ujournal.data.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import com.google.firebase.firestore.FirebaseFirestore


import java.util.UUID

class JournalEntryViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
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

    fun saveJournalEntryToFirestore(userUuid: String, journalEntry: JournalEntry) {
        val journalDocRef = firestore
            .collection("journals")
            .document("users")
            .collection(userUuid)
            .document(journalEntry.id.toString())
        val journalData = mapOf(
            "id" to journalEntry.id.toString(),
            "title" to journalEntry.title,
            "description" to journalEntry.description,
            "imageURI" to journalEntry.imageURI,
            "latitude" to journalEntry.latitude,
            "longitude" to journalEntry.longitude,
            "createdAt" to journalEntry.createdAt,
            "updatedAt" to journalEntry.updatedAt
        )

        journalDocRef.set(journalData)
            .addOnSuccessListener {
                Log.d("Firestore", "Journal entry saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving journal entry", e)
            }
    }

    fun getJournalEntry(journalEntryID: String): JournalEntry? {
        return _journalEntries.value.firstOrNull { it.id.toString() == journalEntryID }
    }

    fun updateJournalEntry(
        journalEntryID: String,
        newTitle: String,
        newDescription: String,
        newImageURI: String?,
        newLatitude: Double?,
        newLongitude: Double?,
        newDate: LocalDateTime?,
        updatedAt: LocalDateTime
    ) {
        Log.d("JournalEntryViewModel", "Updating entry with ID: $journalEntryID")

        _journalEntries.update { currentEntries ->
            currentEntries.map { entry ->
                if (entry.id.toString() == journalEntryID) {
                    entry.copy(
                        title = newTitle,
                        description = newDescription,
                        imageURI = newImageURI,
                        latitude = newLatitude,
                        longitude = newLongitude,
                        createdAt = newDate ?: entry.createdAt,
                        updatedAt = updatedAt
                    )
                } else {
                    entry
                }
            }
        }
    }

    fun updateJournalEntryInFirestore(
        userUuid: String,
        journalEntryID: String,
        newTitle: String,
        newDescription: String,
        newImageURI: String?,
        newLatitude: Double?,
        newLongitude: Double?,
        updatedAt: LocalDateTime,
        newDate: LocalDateTime
    ){
        val journalDocRef = firestore
            .collection("journals")
            .document("users")
            .collection(userUuid)
            .document(journalEntryID)
        val updatedData = mapOf(
            "title" to newTitle,
            "description" to newDescription,
            "imageURI" to newImageURI,
            "latitude" to newLatitude,
            "longitude" to newLongitude,
            "updatedAt" to updatedAt.toString(),
            "createdAt" to newDate.toString()
        )
        journalDocRef.set(updatedData)
            .addOnSuccessListener { Log.d("Firestore", "Journal entry updated") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error updating entry", e) }
    }


    fun remove(journalEntryID: String) {
        Log.d("JournalEntryViewModel", "Removing entry with ID: $journalEntryID")

        _journalEntries.update { currentEntries ->
            currentEntries.filter { it.id.toString() != journalEntryID }
        }
    }

    fun deleteJournalEntryFromFirestore(userUuid: String, journalEntryId: String) {
        val journalDocRef = firestore
            .collection("journals")
            .document("users")
            .collection(userUuid)
            .document(journalEntryId)

        journalDocRef.delete()
            .addOnSuccessListener { Log.d("Firestore", "Journal entry deleted") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error deleting entry", e) }
    }


    fun getJournalEntriesGroupedByDate(
        onlyContainImage : Boolean = false
    ): Map<String, List<JournalEntry>> {
        val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        if(onlyContainImage) {
            return _journalEntries.value
                .filter {
                    !it.imageURI.isNullOrBlank()
                }
                .sortedByDescending { it.createdAt }
                .groupBy { entry ->
                    val date = entry.createdAt.toLocalDate()
                    dateFormatter.format(date)
                }
        }

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
    latitude: Double = 0.78,
    longitude: Double = 113.92,
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
            latitude = -6.31,
            longitude =  106.66 + (index * 0.01),
            createdAt = LocalDateTime.now().minusDays(index.toLong())
        )
    }
}