package id.ac.umn.ujournal.data.model

data class User(
    val id: String,
    var firstName: String,
    var lastName: String?,
    val email: String,
    var profileImageURL: String?,
    var provider: String?,
    var password: String?  // TODO: Remove after integration to backend
)
