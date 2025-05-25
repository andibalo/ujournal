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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.data.model.User
import id.ac.umn.ujournal.ui.util.toDate
import kotlinx.coroutines.tasks.await

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

    override suspend fun getJournalEntryByID(id: String): DocumentSnapshot {
        val document = db.collection("journalEntries")
            .document(id)
            .get()
            .await()

        return document
    }


    override suspend fun getJournalEntries(userID: String): QuerySnapshot {
        val documents = db.collection("journalEntries")
            .whereEqualTo("userId", userID)
            .get()
            .await()

        return documents
    }

    override suspend fun saveJournalEntry(journalEntry: JournalEntry): Task<Void> {
        val createdAt = journalEntry.createdAt.toDate()

        val journalEntryData = hashMapOf(
            "title" to journalEntry.title,
            "description" to journalEntry.description,
            "imageURI" to journalEntry.imageURI,
            "userId" to journalEntry.userId,
            "latitude" to journalEntry.latitude,
            "longitude" to journalEntry.longitude,
            "createdAt" to createdAt,
            "updatedAt" to null,
        )

        return db
                .collection("journalEntries")
                .document(journalEntry.id)
                .set(journalEntryData)
    }

    override suspend fun updateJournalEntryByID(id: String, journalEntry: JournalEntry): Task<Void> {

        val journalEntryUpdateData = mapOf(
            "title" to journalEntry.title,
            "description" to journalEntry.description,
            "imageURI" to journalEntry.imageURI,
            "latitude" to journalEntry.latitude,
            "longitude" to journalEntry.longitude,
            "updatedAt" to FieldValue.serverTimestamp(),
        )

        return db
            .collection("journalEntries")
            .document(id)
            .update(journalEntryUpdateData)
    }

    override suspend fun deleteJournalEntryByID(id: String): Task<Void> {

        return db
                .collection("journalEntries")
                .document(id)
                .delete()
    }

    override suspend fun getUser(userID: String): Task<DocumentSnapshot> {
        return db
                .collection("users")
                .document(userID)
                .get()
    }

    override suspend fun saveUser(user: User): Task<Void> {
        val userData = hashMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "profileImageURL" to user.profileImageURL,
            "provider" to user.provider,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to null
        )

        return db
            .collection("users")
            .document(user.id)
            .set(userData)
    }

    override suspend fun updateUserProfileImageURL(userId: String, imageUri : String) : Task<Void> {
        val userUpdateData = mapOf(
            "profileImageURL" to imageUri,
        )

        return db
            .collection("users")
            .document(userId)
            .update(userUpdateData)
    }


    override fun logout() {
        return auth.signOut()
    }

    override fun getCurrentUser() : FirebaseUser? {
        return auth.currentUser
    }

}
