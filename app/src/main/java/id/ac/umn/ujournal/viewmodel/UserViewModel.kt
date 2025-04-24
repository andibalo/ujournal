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

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModel : ViewModel() {

    // TODO: remove _users after backend integration
    private val _users  = MutableStateFlow(getInitialUserListData())

    val users: StateFlow<List<User>> get() = _users

    // TODO: remove user dummy data
    private val _userDummyData = MutableStateFlow<User?>(generateInitialUser())

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> get() = _userState

    fun loadUserData() {
        viewModelScope.launch {
            try {
                val user = fetchUserFromRemote()

                if(user == null) {
                    throw Exception("User not found")
                }

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

    private suspend fun fetchUserFromRemote(): User? {
        kotlinx.coroutines.delay(1000)
        return _userDummyData.value
    }

    // TODO: remove after backend integration
    fun register(user: User) {

        val currentUserList = _users.value

        val existingUser = currentUserList.firstOrNull { u ->
            u.email == user.email
        }

        if(existingUser != null) {
            throw Exception("User already exists")
        }

        Log.d("UserViewModel.addToUserList", "Current List: $currentUserList")

        _users.update { currentEntries ->
            listOf(user) + currentEntries
        }

        this.updateUserData(user)
    }

    // TODO: remove after backend integration
    fun login(email: String, password: String)  {
        Log.d("UserViewModel.login", "Current List: ${_users.value}")

        val user = _users.value.firstOrNull { user ->
            user.email == email  && user.password == password
        }

        if(user == null) {
            throw Exception("User not found")
        }

        _userDummyData.update {
            user
        }
    }

    // TODO: remove after backend integration
    fun setUserData(email: String)  {
        Log.d("UserViewModel.setUserData", "Current List: ${_users.value}")

        val user = _users.value.firstOrNull { user ->
            user.email == email
        }

        if(user == null) {
            throw Exception("User not found")
        }

        _userDummyData.update {
            user
        }
    }

    // TODO: remove after backend integration
    fun logout()  {
        _userDummyData.update {
            null
        }
    }
}

// TODO: remove after backend integration
private fun generateInitialUser(): User {
    return User(
        id = UUID.randomUUID(),
        firstName = "John",
        lastName = "Doe",
        email = "test@gmail.com",
        password = "123456",
        profileImageURL = "https://randomuser.me/api/portraits/men/75.jpg",
    )
}

// TODO: remove after backend integration
fun getInitialUserListData(): List<User> {
    return List(1) { index ->
        generateInitialUser()
    }
}