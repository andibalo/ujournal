package id.ac.umn.ujournal.ui.components.journalentry

import java.time.LocalDateTime
import java.util.UUID

class JournalEntry(
    val id: UUID,
    val title: String,
    val description: String,
    val picture: String,
    val geotag: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)