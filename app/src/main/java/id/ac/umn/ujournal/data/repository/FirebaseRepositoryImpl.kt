package id.ac.umn.ujournal.data.repository

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.data.model.User

class FirebaseRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,

): FirebaseRepository {

    override suspend fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun register(email : String, password : String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun loginWithGoogle(credential: Credential): Task<AuthResult> {
        if (credential is CustomCredential && credential.type != TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            throw Exception("Credential is not of type Google ID!")
        }

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

        val googleCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

        return auth.signInWithCredential(googleCredential)
    }

    override suspend fun saveJournalEntry(journalEntry: JournalEntry): Task<DocumentReference> {
        val journalEntryData = hashMapOf(
            "title" to journalEntry.title,
            "description" to journalEntry.description,
            "imageURI" to journalEntry.imageURI,
            "latitude" to journalEntry.latitude,
            "longitude" to journalEntry.longitude,
            "createdAt" to journalEntry.createdAt,
        )

        return db
                .collection("journalEntries")
                .add(journalEntryData)
    }

    override suspend fun saveUser(user: User): Task<DocumentReference> {
        val userData = hashMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "profileImageURL" to user.profileImageURL,
            "provider" to user.provider,
        )

        return db
            .collection("users")
            .add(userData)
    }


    override fun logout() {
        return auth.signOut()
    }

    override fun getCurrentUser() : FirebaseUser? {
        return auth.currentUser
    }

}
