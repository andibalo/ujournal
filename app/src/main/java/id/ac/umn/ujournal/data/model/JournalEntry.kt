package id.ac.umn.ujournal.data.model

import java.time.LocalDateTime
import java.util.UUID

data class JournalEntry(
    val id: String,
    var userId: String,
    var title: String,
    var description: String,
    var imageURI: String?,
    var latitude: Double?,
    var longitude: Double?,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
)