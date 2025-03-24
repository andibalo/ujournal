package id.ac.umn.ujournal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.umn.ujournal.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private fun generateInitialUser(): User {
    return User(
        id = UUID.randomUUID(),
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        password = "password123",
        profileImageURL = "TEST",
    )
}

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModel : ViewModel() {

    // TODO: remove user dummy data
    private val _userDummyData = MutableStateFlow(generateInitialUser())

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> get() = _userState

    fun loadUserData() {
        viewModelScope.launch {
            try {
                val user = fetchUserFromRemote()
                _userState.value = UserState.Success(user)
            } catch (e: Exception) {
                _userState.value = UserState.Error("Failed to load user")
            }
        }
    }

    fun updateUserData(user: User) {
        Log.d("UserViewModel", "Updating user data dummy: $user")

        _userState.update {
            UserState.Loading
        }

        _userDummyData.update {
           user
        }

        _userState.update {
            UserState.Success(user)
        }
    }

    private suspend fun fetchUserFromRemote(): User {
        kotlinx.coroutines.delay(1000)
        return _userDummyData.value
    }
}
