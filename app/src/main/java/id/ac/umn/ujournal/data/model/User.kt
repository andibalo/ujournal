package id.ac.umn.ujournal.data.model

import java.time.LocalDateTime

data class User(
    val id: String,
    var firstName: String,
    var lastName: String?,
    val email: String,
    var profileImageURL: String?,
    var provider: String?,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
)
