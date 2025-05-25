package id.ac.umn.ujournal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.data.model.User
import id.ac.umn.ujournal.data.repository.FirebaseRepository
import id.ac.umn.ujournal.ui.util.toLocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

import java.util.UUID

sealed class JournalEntryState {
    object Loading : JournalEntryState()
    data class Success(val user: JournalEntry) : JournalEntryState()
    data class Error(val message: String) : JournalEntryState()
}

class JournalEntryViewModel(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val _journalEntries = MutableStateFlow(getJournalEntryTestData())

    val journalEntries: StateFlow<List<JournalEntry>> get() = _journalEntries

    private val _journalEntryState = MutableStateFlow<JournalEntryState>(JournalEntryState.Loading)
    val journalEntryState: StateFlow<JournalEntryState> get() = _journalEntryState

    suspend fun getJournalEntries(): List<JournalEntry> {

        _journalEntryState.value = JournalEntryState.Loading

        try {
            val documents = firebaseRepository.getJournalEntries()

            val journalEntriesList = documents.map { document ->

                val createdAt = document.data["createdAt"] as com.google.firebase.Timestamp
                val updatedAt = document.data["updatedAt"] as? com.google.firebase.Timestamp

                JournalEntry(
                    id = document.id,
                    userId = document.data["userId"] as String,
                    title = document.data["title"] as String,
                    description = document.data["description"] as String,
                    imageURI = document.data["imageURI"] as String?,
                    latitude = document.data["latitude"] as Double?,
                    longitude = document.data["longitude"] as Double?,
                    createdAt = createdAt.toLocalDateTime(),
                    updatedAt  = updatedAt?.toLocalDateTime(),
                )
            }

            _journalEntries.value = journalEntriesList

            return _journalEntries.value
        } catch (e:Exception){
            _journalEntryState.value = JournalEntryState.Error("Failed to get journal entries")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    suspend fun addJournalEntry(entry: JournalEntry) {
        Log.d("JournalEntryViewModel", "Adding entry: $entry")

        val currentList = _journalEntries.value
        Log.d("JournalEntryViewModel", "Current List: $currentList")

        _journalEntryState.value = JournalEntryState.Loading

        try{
            firebaseRepository.saveJournalEntry(entry).await()

            _journalEntryState.value = JournalEntryState.Success(entry)

        } catch (e:Exception){
            _journalEntryState.value = JournalEntryState.Error("Failed to save journal entry")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    fun getJournalEntry(journalEntryID: String): JournalEntry? {
        return _journalEntries.value.firstOrNull { it.id.toString() == journalEntryID }
    }

    suspend fun deleteJournalEntryByID(journalEntryID: String) {
        _journalEntryState.value = JournalEntryState.Loading

        try{
            firebaseRepository.deleteJournalEntryByID(journalEntryID).await()
        } catch (e:Exception){
            _journalEntryState.value = JournalEntryState.Error("Failed to delete journal entry")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    suspend fun getJournalEntryByID(journalEntryID: String): JournalEntry? {
        _journalEntryState.value = JournalEntryState.Loading

        try{
            val document = firebaseRepository.getJournalEntryByID(journalEntryID)
            Log.d("TEST", "DocumentSnapshot data: ${document.data}")
            if (document.exists()) {
                val createdAt = document.data!!["createdAt"] as com.google.firebase.Timestamp
                val updatedAt = document.data!!["updatedAt"] as? com.google.firebase.Timestamp


                val journalEntry = JournalEntry(
                    id = document.id,
                    userId = document.data!!["userId"] as String,
                    title = document.data!!["title"] as String,
                    description = document.data!!["description"] as String,
                    imageURI = document.data!!["imageURI"] as String?,
                    latitude = document.data!!["latitude"] as Double?,
                    longitude = document.data!!["longitude"] as Double?,
                    createdAt = createdAt.toLocalDateTime(),
                    updatedAt  = updatedAt?.toLocalDateTime(),
                )

                return journalEntry
            }

            return null

        } catch (e:Exception){
            _journalEntryState.value = JournalEntryState.Error("Failed to get journal entry")
            throw Exception(e.message?:"Something went wrong")
        }
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
    id: String = UUID.randomUUID().toString(),
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
        updatedAt = updatedAt,
        userId = "c57dbGjPzcOe10Ma7yGh5CWh6vs1"
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