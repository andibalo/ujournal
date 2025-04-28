package id.ac.umn.ujournal.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth
): FirebaseAuthRepository {

    override suspend fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun register(email : String, password : String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun loginWithGoogle(credential: AuthCredential): Task<AuthResult> {
        return auth.signInWithCredential(credential)
    }

    override fun logout() {
        return auth.signOut()
    }

    override fun getCurrentUser() : FirebaseUser? {
        return auth.currentUser
    }

}
