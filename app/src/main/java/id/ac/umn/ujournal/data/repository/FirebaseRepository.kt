package id.ac.umn.ujournal.data.repository

import androidx.credentials.Credential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.data.model.User

interface FirebaseRepository {
    suspend fun login(email: String, password: String): Task<AuthResult>
    suspend fun register(email : String, password : String): Task<AuthResult>
    suspend fun loginWithGoogle(credential: Credential): Task<AuthResult>
    suspend fun saveJournalEntry(journalEntry: JournalEntry):  Task<DocumentReference>
    suspend fun saveUser(user: User): Task<DocumentReference>
    fun logout()
    fun getCurrentUser(): FirebaseUser?
}