package id.ac.umn.ujournal.data.repository

import androidx.credentials.Credential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.data.model.User

interface FirebaseRepository {
    suspend fun login(email: String, password: String): Task<AuthResult>
    suspend fun register(email : String, password : String): Task<AuthResult>
    suspend fun loginWithGoogle(credential: Credential): Task<AuthResult>
    suspend fun getJournalEntryByID(id: String): DocumentSnapshot
    suspend fun getJournalEntries(): QuerySnapshot
    suspend fun saveJournalEntry(journalEntry: JournalEntry): Task<Void>
    suspend fun deleteJournalEntryByID(id: String) : Task<Void>
    suspend fun saveUser(user: User): Task<Void>
    suspend fun getUser(userID: String): Task<DocumentSnapshot>
    fun logout()
    fun getCurrentUser(): FirebaseUser?
}