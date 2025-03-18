package id.ac.umn.ujournal.ui.journal

import java.time.LocalDateTime
import java.util.UUID

data class JournalEntry(
    val id: UUID,
    var title: String,
    var description: String,
    var imageURI: String?,
    var geotag: List<String>,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
)