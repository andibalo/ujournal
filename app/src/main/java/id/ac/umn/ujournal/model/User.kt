package id.ac.umn.ujournal.model

import java.util.UUID

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val profileImageURL: String?,
    val password: String = "admin" // TODO: Remove after integration to backend
)
