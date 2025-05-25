package id.ac.umn.ujournal.viewmodel

import androidx.lifecycle.ViewModel
import id.ac.umn.ujournal.data.model.User
import id.ac.umn.ujournal.data.repository.FirebaseRepository
import id.ac.umn.ujournal.ui.util.toLocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModel(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _user  = MutableStateFlow<User?>(null)

    val user: StateFlow<User?> get() = _user

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> get() = _userState

    suspend fun loadUserData(): User {
        if(firebaseRepository.getCurrentUser() == null){
            throw Exception("Current user does not exist")
        }

        return fetchUserFromFirebase(firebaseRepository.getCurrentUser()!!.uid)
    }

    suspend fun updateProfileImage(userId: String, imageUri: String) {
        _userState.value = UserState.Loading

        try{
            firebaseRepository.updateUserProfileImageURL(userId, imageUri).await()

            _user.value = _user.value?.copy(profileImageURL = imageUri)

        } catch (e:Exception){
            _userState.value = UserState.Error("Failed to update user profile image")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    suspend fun saveUser(user : User){

        _userState.value = UserState.Loading

        try{
            firebaseRepository.saveUser(user).await()

            _user.value = user

            _userState.value = UserState.Success(user)

        } catch (e:Exception){
            _userState.value = UserState.Error("Failed to save user")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    suspend fun fetchUserFromFirebase(userID : String) : User {
        _userState.value = UserState.Loading

        try{
            val document = firebaseRepository.getUser(userID).await()

            if (!document.exists()) {
                throw Exception("User not found")
            }

            val createdAt = document.data!!["createdAt"] as com.google.firebase.Timestamp
            val updatedAt = document.data!!["updatedAt"] as? com.google.firebase.Timestamp

            val user = User(
                id = document.id,
                firstName = document.data!!["firstName"] as String,
                lastName = document.data!!["lastName"] as String?,
                email = document.data!!["email"] as String,
                profileImageURL  = document.data!!["profileImageURL"] as String?,
                provider  = document.data!!["provider"] as String?,
                createdAt = createdAt.toLocalDateTime(),
                updatedAt  = updatedAt?.toLocalDateTime(),
            )

            _user.value = user

            _userState.value = UserState.Success(user)
            return user
        } catch (e:Exception){
            _userState.value = UserState.Error("Failed to save user")
            throw Exception(e.message?:"Something went wrong")
        }
    }

    fun logout()  {
        _user.update {
            null
        }
    }
}