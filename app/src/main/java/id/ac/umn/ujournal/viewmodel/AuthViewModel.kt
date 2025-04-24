package id.ac.umn.ujournal.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

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

    suspend fun login(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            throw Exception("Email or password can't be empty")
        }

        _authState.value = AuthState.Loading

        try{
            auth.signInWithEmailAndPassword(email,password).await()
            _authState.value = AuthState.Authenticated
        } catch (e:Exception){
            _authState.value = AuthState.Error(e.message?:"Something went wrong")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    suspend fun register(email : String, password : String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            throw Exception("Email or password can't be empty")
        }

        _authState.value = AuthState.Loading

        try{
            _authState.value = AuthState.Loading
            auth.createUserWithEmailAndPassword(email,password).await()
            _authState.value = AuthState.Authenticated
        } catch (e:Exception){
            _authState.value = AuthState.Error(e.message?:"Something went wrong")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    fun logout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}