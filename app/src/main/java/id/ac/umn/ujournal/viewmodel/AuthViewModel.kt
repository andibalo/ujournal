package id.ac.umn.ujournal.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import id.ac.umn.ujournal.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    val credentialManager: CredentialManager,
) : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    suspend fun firebaseAuthBasicLogin(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            throw Exception("Email or password can't be empty")
        }

        _authState.value = AuthState.Loading

        try{
            auth.signInWithEmailAndPassword(email,password).await()
        } catch (e:Exception){
            _authState.value = AuthState.Error(e.message?:"Something went wrong")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    suspend fun firebaseAuthBasicRegister(email : String, password : String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            throw Exception("Email or password can't be empty")
        }

        _authState.value = AuthState.Loading

        try{
            auth.createUserWithEmailAndPassword(email,password).await()
        } catch (e:Exception){
            _authState.value = AuthState.Error(e.message?:"Something went wrong")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    fun setAuthStatus(isAuthenticated: Boolean) {
        if(isAuthenticated) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    suspend fun firebaseAuthWithGoogle(context: Context): FirebaseUser? {
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(context.getString(R.string.default_web_client_id))
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        _authState.value = AuthState.Loading

        try {
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential

            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                // Create Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                auth.signInWithCredential(firebaseCredential).await()

                return auth.currentUser
            } else {
                throw Exception("Credential is not of type Google ID!")
            }

        } catch (e : Exception) {
            Log.d("AuthViewModel", e.toString())

            _authState.value = AuthState.Error(e.message?:"Something went wrong")

            throw Exception(e.message?:"Something went wrong")
        }
    }

    fun logout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated

        viewModelScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)

            } catch (e: ClearCredentialException) {
                Log.e("AuthViewModel", "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }
}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}