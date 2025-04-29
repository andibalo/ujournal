package id.ac.umn.ujournal.data.repository

import androidx.credentials.Credential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepository {
    suspend fun login(email: String, password: String): Task<AuthResult>
    suspend fun register(email : String, password : String): Task<AuthResult>
    suspend fun loginWithGoogle(credential: Credential): Task<AuthResult>
    fun logout()
    fun getCurrentUser(): FirebaseUser?
}