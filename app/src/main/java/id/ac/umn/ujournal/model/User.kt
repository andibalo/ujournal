package id.ac.umn.ujournal.model

import java.util.UUID

data class User(
    val id: UUID,
    var firstName: String,
    var lastName: String,
    val email: String,
    var profileImageURL: String?,
    var password: String  // TODO: Remove after integration to backend
)
